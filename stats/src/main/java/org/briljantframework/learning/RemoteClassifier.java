/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.learning;

import com.google.common.collect.Lists;
import org.briljantframework.communication.Protocol;
import org.briljantframework.communication.Streams;
import org.briljantframework.data.DataFrame;
import org.briljantframework.data.Row;
import org.briljantframework.data.RowView;
import org.briljantframework.data.Traversable;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.types.CategoricType;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.types.Types;
import org.briljantframework.data.values.Categoric;
import org.briljantframework.data.values.Missing;
import org.briljantframework.data.values.Numeric;
import org.briljantframework.io.BinaryOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.msgpack.unpacker.Unpacker;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The type Remote classifier.
 *
 * @author Isak Karlsson
 */
public class RemoteClassifier implements Classifier<Row, DataFrame<Row>, Column> {

    private static final MessagePack messagePack = new MessagePack();
    private static Logger logger = Logger.getLogger(RemoteClassifier.class.getSimpleName());
    private final Map<String, Object> options;

    private final Protocol learn;
    private final Protocol predict;
    private final String name;

    /**
     * Instantiates a new Remote classifier.
     *
     * @param name    the name
     * @param builder the builder
     */
    protected RemoteClassifier(String name, Builder builder) {
        this.learn = builder.learner;
        this.predict = builder.predicter;
        this.options = builder.options;
        this.name = name;
    }

    /**
     * With builder.
     *
     * @param learner   the learner
     * @param predicter the predicter
     * @return the builder
     */
    public static Builder with(Protocol learner, Protocol predicter) {
        return with("RemoteClassifier", learner, predicter);
    }

    /**
     * With builder.
     *
     * @param name      the name
     * @param learner   the learner
     * @param predicter the predicter
     * @return the builder
     */
    public static Builder with(String name, Protocol learner, Protocol predicter) {
        return new Builder(name, learner, predicter);
    }

    /**
     * Set options to the external classifier -- this is the actual values that will be sent to the classifier
     *
     * @param options - Map of options
     * @return this remote classifier
     */
    public RemoteClassifier set(Map<String, Object> options) {
        this.options.putAll(options);
        return this;
    }

    /**
     * Set remote classifier.
     *
     * @param key   - parameter key
     * @param value - parameter valueOf
     * @return this remote classifier
     */
    public RemoteClassifier set(String key, Object value) {
        this.options.put(key, value);
        return this;
    }

    @Override
    public Model fit(DataFrame<Row> dataFrame, Column column) {
        try (Streams streams = this.learn.create()) {
            Thread thread = new Thread(() -> {
                if (streams.getErrorStream() != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(streams.getErrorStream()))) {
                        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        logger.severe(e.getMessage());
                    }
                }
            });
            thread.start();

            try (OutputStream output = new BufferedOutputStream(streams.getOutputStream())) {
                // STEP 1: Send a map of parameters
                Packer packer = messagePack.createPacker(output);
                packer.write(options);

                // STEP 2: Write the dataset to the output valueStream!
                transfer(dataFrame, output);
            }

            // STEP 3: Wait for response.
            try (InputStream input = new BufferedInputStream(streams.getInputStream())) {
                Unpacker unpacker = messagePack.createUnpacker(input);

                // STEP 4: The first response should be a boolean. If true - an error occured
                if (unpacker.readBoolean()) {
                    thread.join();

                    // STEP 4a: Throw a runtime exception with the error reason
                    throw new RuntimeException(unpacker.readString());
                } else {
                    // STEP 4b: receive and return a model
                    Model classification = receive(input);
                    thread.join();
                    return classification;
                }
            } catch (Exception e) {
                thread.interrupt();
                throw new RuntimeException("invalid response from remote classifier", e);
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Transfer void.
     *
     * @param dataset the dataset
     * @param stream  the stream
     * @throws IOException the iO exception
     */
    protected void transfer(Traversable dataset, OutputStream stream) throws IOException {
        Packer packer = messagePack.createPacker(stream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BinaryOutputStream bios = new BinaryOutputStream(baos)) {
            bios.write(dataset);
        }
        byte[] data = baos.toByteArray();
        packer.write(data);
    }

    /**
     * Receive remote classification.
     *
     * @param stream the stream
     * @return remote classification
     * @throws IOException the iO exception
     */
    protected Model receive(InputStream stream) throws IOException {
        Unpacker unpacker = messagePack.createUnpacker(stream);
        byte[] model = unpacker.readByteArray();
        return new Model(model, this.predict);
    }

    @Override
    public String toString() {
        return String.format("%s(%s, %s)", name, learn, predict);
    }

    /**
     * The type Builder.
     */
    public static class Builder implements Classifier.Builder<RemoteClassifier> {
        private final String name;
        private HashMap<String, Object> options = new HashMap<>();
        private Protocol learner;
        private Protocol predicter;

        private Builder(String name, Protocol learner, Protocol predicter) {
            this.learner = learner;
            this.predicter = predicter;
            this.name = name;
        }

        /**
         * Set builder.
         *
         * @param key   the key
         * @param value the value
         * @return the builder
         */
        public Builder set(String key, Object value) {
            options.put(key, value);
            return this;
        }

        /**
         * Build remote classifier.
         *
         * @return the remote classifier
         */
        @Override
        public RemoteClassifier create() {
            return new RemoteClassifier(name, this);
        }
    }

    /**
     * The remote process is required to send the predictions in the same order they arrived!
     * <p>
     * TODO - regression and classification targets
     */
    public static class Model implements org.briljantframework.learning.Model<Row, DataFrame<Row>> {

        /**
         * The constant PROBABILITY.
         */
        public static final Value PROBABILITY = ValueFactory.createRawValue("probability");
        /**
         * The constant LABEL.
         */
        public static final Value LABEL = ValueFactory.createRawValue("label");
        private static final MessagePack messagePack = new MessagePack();

        /**
         * The Model.
         */
        public final byte[] model;

        private Protocol protocol;


        /**
         * Instantiates a new Model.
         *
         * @param model    the model
         * @param protocol the protocol
         */
        public Model(byte[] model, Protocol protocol) {
            this.model = model;
            this.protocol = protocol;
        }

        @Override
        public Predictions predict(DataFrame<Row> dataFrame) {
            try (Streams streams = protocol.create()) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BinaryOutputStream bios = new BinaryOutputStream(baos);
                bios.write(new MissingTarget(dataFrame));
                byte[] data = baos.toByteArray();
                bios.close();

                Thread thread = new Thread(() -> {
                    if (streams.getErrorStream() != null) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(streams.getErrorStream()))) {
                            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                                System.out.println(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

                try (Packer packer = messagePack.createPacker(streams.getOutputStream())) {
                    packer.write(dataFrame.rows());
                    packer.write(data);
                    packer.write(model);
                }
                thread.join();

                try (Unpacker unpacker = messagePack.createUnpacker(streams.getInputStream())) {
                    boolean error = unpacker.readBoolean();
                    if (error) {
                        String message = unpacker.readString();
                        throw new RuntimeException(String.format("Predictor %s failed with %s", protocol.toString(), message));
                    }
                    Value value = unpacker.readValue();

                    if (value.isArrayValue()) {
                        ArrayValue remotePredictions = value.asArrayValue();
                        if (remotePredictions.size() != dataFrame.rows()) {
                            throw new RuntimeException("remotePredictions.size() != dataset.examples()");
                        }
                        return Predictions.create(remotePredictions.stream().map(this::createPrediction).collect(Collectors.toList()));
                    } else {
                        throw new RuntimeException(String.format("got %s, but expected array!", value.getType().toString()));
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        public Prediction predict(Row row) {
            return null;
        }

        /**
         * Create prediction.
         * <p>
         * TODO(isak) - create predictions should support regression
         *
         * @param remoteValue the remote value
         * @return the prediction
         */
        protected Prediction createPrediction(Value remoteValue) {
            List<org.briljantframework.data.values.Value> target = new ArrayList<>();
            List<Double> probabilities = new ArrayList<>();

            ArrayValue array = remoteValue.asArrayValue();
            for (Value value : array) {
                MapValue pred = value.asMapValue();
                org.briljantframework.data.values.Value label;
                if (!pred.get(LABEL).isFloatValue()) {
                    Object repr;
                    if (pred.get(LABEL).isIntegerValue()) {
                        repr = pred.get(LABEL).asIntegerValue().getInt();
                    } else {
                        repr = pred.get(LABEL).asRawValue().getString();
                    }
                    label = Categoric.valueOf(repr);
                } else {
                    label = Numeric.valueOf(pred.get(LABEL).asFloatValue().getDouble());
                }
                double prob = pred.get(PROBABILITY).asFloatValue().getDouble();
                target.add(label);
                probabilities.add(prob);
            }

            return Prediction.nominal(target, probabilities);
        }

        // TODO - this might be usefull
        private static final class MissingTarget implements Traversable<Row> {

            private final DataFrame<?> dataFrame;

            private MissingTarget(DataFrame<?> dataFrame) {
                this.dataFrame = dataFrame;
            }

            @Override
            public Type getType(int col) {
                return getTypes().get(col);
            }

            @Override
            public Types getTypes() {
                ArrayList<Type> types = Lists.newArrayList(dataFrame.getTypes());
                types.add(new CategoricType("class"));
                return Types.withoutCopying(types);
            }

            @Override
            public int columns() {
                return dataFrame.columns() + 1;
            }


            @Override
            public Iterator<Row> iterator() {
                return new Iterator<Row>() {
                    Iterator<? extends Row> datasetIterator = dataFrame.iterator();

                    @Override
                    public boolean hasNext() {
                        return datasetIterator.hasNext();
                    }

                    @Override
                    public RowView next() {
                        Row row = datasetIterator.next();

                        return new RowView(null, 0) {

                            @Override
                            public Iterator<org.briljantframework.data.values.Value> iterator() {
                                return new Iterator<org.briljantframework.data.values.Value>() {
                                    Iterator<org.briljantframework.data.values.Value> entryValueIterator = row.iterator();
                                    boolean hasNext = true;

                                    @Override
                                    public boolean hasNext() {
                                        return hasNext;
                                    }

                                    @Override
                                    public org.briljantframework.data.values.Value next() {
                                        if (entryValueIterator.hasNext()) {
                                            hasNext = true;
                                            return entryValueIterator.next();
                                        } else {
                                            hasNext = false;
                                            return Missing.valueOf();
                                        }
                                    }
                                };
                            }

                            @Override
                            public org.briljantframework.data.values.Value getValue(int col) {
                                if (col < row.size()) {
                                    return row.getValue(col);
                                } else {
                                    return Missing.valueOf();
                                }
                            }

                            @Override
                            public int size() {
                                return row.size() + 1;
                            }
                        };
                    }
                };
            }
        }

        @Override
        public String toString() {
            return Arrays.toString(model).substring(0, model.length > 9 ? 9 : model.length) + ",...";
        }


    }
}
