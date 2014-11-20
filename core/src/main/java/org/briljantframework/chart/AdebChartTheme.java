package org.briljantframework.chart;

import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;

import java.awt.*;

/**
 * Created by Isak Karlsson on 14/10/14.
 */
public final class AdebChartTheme extends StandardChartTheme {

    private final Paint[] colors;

    /**
     * Instantiates a new Adeb chart theme.
     *
     * @param name the name
     */
    public AdebChartTheme(String name) {
        this(name, false);
    }

    /**
     * Instantiates a new Adeb chart theme.
     *
     * @param name   the name
     * @param shadow the shadow
     */
    public AdebChartTheme(String name, boolean shadow) {
        super(name, shadow);
        setBarPainter(new StandardBarPainter());
        setXYBarPainter(new StandardXYBarPainter());

        setChartBackgroundPaint(Color.white);
        setPlotBackgroundPaint(Color.white);
        setPlotOutlinePaint(Color.black);
        setDomainGridlinePaint(Color.darkGray);
        setRangeGridlinePaint(Color.darkGray);

        String font = "CMU Serif";
        setLargeFont(new Font(font, Font.PLAIN, 11));
        setExtraLargeFont(new Font(font, Font.BOLD, 12));
        setRegularFont(new Font(font, Font.PLAIN, 9));
        setSmallFont(new Font(font, Font.PLAIN, 7));


        colors = new Paint[]{
                new Color(0, 55, 255, 180),
                new Color(255, 172, 0, 180),
                new Color(128, 0, 255, 180),
                new Color(0, 205, 0, 180),
                new Color(205, 0, 0, 180),
                new Color(255, 215, 0, 180),
                new Color(255, 0, 255, 180),
                new Color(255, 166, 201, 180),
                new Color(207, 207, 207, 180),
                new Color(0, 255, 255, 180),
                new Color(102, 56, 10, 180),
                new Color(0, 0, 0, 180)
        };

        setShadowVisible(false);
        setDrawingSupplier(new DefaultDrawingSupplier(
                colors, colors,
                new Stroke[]{new BasicStroke(1.0f)},
                new Stroke[]{new BasicStroke(0.5f)},
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE
        ));
    }

    /**
     * Create adeb theme.
     *
     * @return the adeb chart theme
     */
    public static AdebChartTheme createAdebTheme() {
        return new AdebChartTheme("AdebChartTheme");
    }

    public Paint getColor(int i) {
        return colors[i];
    }
}
