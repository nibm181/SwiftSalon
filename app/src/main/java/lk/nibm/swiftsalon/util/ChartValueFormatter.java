package lk.nibm.swiftsalon.util;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class ChartValueFormatter extends ValueFormatter {

    public DecimalFormat mFormat;
    private PieChart pieChart;
    private boolean percentSignSeparated;

    public ChartValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0");
        percentSignSeparated = true;
    }

    // Can be used to remove percent signs if the chart isn't in percent mode
    public ChartValueFormatter(PieChart pieChart) {
        this();
        this.pieChart = pieChart;
    }

    // Can be used to remove percent signs if the chart isn't in percent mode
    public ChartValueFormatter(PieChart pieChart, boolean percentSignSeparated) {
        this(pieChart);
        this.percentSignSeparated = percentSignSeparated;
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + (percentSignSeparated ? " %" : "%");
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        if (pieChart != null && pieChart.isUsePercentValuesEnabled()) {
            // Converted to percent
            return getFormattedValue(value);
        } else {
            // raw value, skip percent sign
            return mFormat.format(value);
        }
    }
}
