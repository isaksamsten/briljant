import org.briljantframework.array.netlib.NetlibArrayBackend;

// Construct the array backend
ArrayBackend mb = NetlibArrayBackend.getInstance();

// Get the array factory
ArrayFactory bj = mb.getArrayFactory();

// Get the array routines specialized for the array factory
ArrayRoutines bjr = mb.getArrayRoutines();

DoubleArray x = bj.array(new double[]{1, 5, 9, 2, 6, 10, 3, 7, 11, 4, 8, 12 }).reshape(4, 3);
System.out.println(x);

// Create an empty result array
DoubleArray c = bj.doubleArray(3, 3);

// Perform generalized matrix-matrix multiplication
// while transposing the first argument
bjr.gemm(Op.TRANSPOSE, Op.KEEP, 1, x, x, 1, c);
System.out.println(c);

double sum = bjr.sum(c);
System.out.println(sum);

// The example above is overly verbose
bjr.dot(Op.TRANSPOSE, Op.KEEP, x,  x);
bjr.dot(x.transpose(), x)
