package org.briljantframework.chart;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

/**
 * Created by Isak Karlsson on 13/10/14.
 */
public interface Chartable {

  /**
   * The constant theme.
   */
  public static final AdebChartTheme theme = AdebChartTheme.createAdebTheme();

  /**
   * Apply theme.
   *
   * @param chart the chart
   * @return the j free chart
   */
  public static JFreeChart applyTheme(JFreeChart chart) {
    theme.apply(chart);
    return chart;
  }

  /**
   * Create chart from plot, applying the theme
   *
   * @param name the name
   * @param plot the plot
   * @return the j free chart
   */
  public static JFreeChart create(String name, Plot plot) {
    JFreeChart chart = new JFreeChart(name, plot);
    return applyTheme(chart);
  }

  /**
   * Create chart from plot, applying the theme
   *
   * @param name the name
   * @param plot the plot
   * @param titleFont the title font
   * @param bool the bool
   * @return the j free chart
   */
  public static JFreeChart create(String name, Plot plot, Font titleFont, boolean bool) {
    JFreeChart chart = new JFreeChart(name, titleFont, plot, bool);
    return applyTheme(chart);
  }

  /**
   * Save void.
   *
   * @param fileName the file name
   * @param chart the chart
   * @param width the width
   * @param height the height
   * @throws IOException the iO exception
   */
  public static void saveSVG(String fileName, JFreeChart chart, int width, int height)
      throws IOException {
    SVGGraphics2D g2 = new SVGGraphics2D(width, height);
    Rectangle2D rect = new Rectangle2D.Double(0, 0, width, height);
    chart.draw(g2, rect);
    SVGUtils.writeToSVG(new File(fileName), g2.getSVGElement());
  }

  /**
   * Save sVG.
   *
   * @param fileName the file name
   * @param chart the chart
   * @throws IOException the iO exception
   */
  public static void saveSVG(String fileName, JFreeChart chart) throws IOException {
    saveSVG(fileName, chart, 500, 500);
  }

  /**
   * Plot boolean.
   *
   * @return the boolean
   */
  default JFreeChart getChart() {
    return create(null, getPlot());
  }

  /**
   * Gets plot.
   *
   * @return the plot
   */
  Plot getPlot();
}
