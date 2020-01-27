/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package org.omnetpp.scave.charting.properties;

import org.omnetpp.common.properties.ColorPropertyDescriptor;
import org.omnetpp.common.properties.PropertySource;
import org.omnetpp.scave.ScaveImages;

public class LineVisualProperties extends PropertySource {

    public static final String
        PROP_DISPLAY_NAME       = "Line.Name",
        PROP_DISPLAY_LINE       = "Line.Display",
        PROP_SYMBOL_TYPE        = "Symbols.Type",
        PROP_SYMBOL_SIZE        = "Symbols.Size",
        PROP_LINE_TYPE          = "Line.Type",
        PROP_LINE_COLOR         = "Line.Color";


    public enum SymbolType {
        None("None", ScaveImages.IMG_OBJ16_SYM_NONE),
        Cross("Cross", ScaveImages.IMG_OBJ16_SYM_CROSS),
        Diamond("Diamond", ScaveImages.IMG_OBJ16_SYM_DIAMOND),
        Dot("Dot", ScaveImages.IMG_OBJ16_SYM_DOT),
        Plus("Plus", ScaveImages.IMG_OBJ16_SYM_PLUS),
        Square("Square", ScaveImages.IMG_OBJ16_SYM_SQUARE),
        Triangle("Triangle", ScaveImages.IMG_OBJ16_SYM_TRIANGLE);

        private String name;
        private String imageId;

        private SymbolType(String name, String img) {
            this.name = name;
            imageId = img;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getImageId() {
            return imageId;
        }
    }

    public enum LineType {
        Linear("linear", ScaveImages.IMG_OBJ16_LINE_LINEAR),
        Pins("pins", ScaveImages.IMG_OBJ16_LINE_PINS),
        Dots("dots", ScaveImages.IMG_OBJ16_LINE_DOTS),
        Points("points", ScaveImages.IMG_OBJ16_LINE_POINTS),
        StepsPost("steps-post", ScaveImages.IMG_OBJ16_LINE_STEPS_POST),
        StepsPre("steps-pre", ScaveImages.IMG_OBJ16_LINE_STEPS_PRE);

        private String name;
        private String imageId;

        private LineType(String name, String img) {
            this.name = name;
            this.imageId = img;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getImageId() {
            return imageId;
        }
    }
    private final PlotProperties chartProps;
    private String lineId;

    public LineVisualProperties(PlotProperties chartProps, String lineId) {
        this.chartProps = chartProps;
        this.lineId = lineId;
    }

    private String propertyName(String baseName) {
        return lineId == null ? baseName : baseName + "/" + lineId;
    }

    @org.omnetpp.common.properties.Property(category="Lines",id=PROP_DISPLAY_NAME,optional=true,
            description="Display name of the line.")
    public String getDisplayName() { return chartProps.getStringProperty(propertyName(PROP_DISPLAY_NAME)); }
    public void setDisplayName(String name) { chartProps.setProperty(propertyName(PROP_DISPLAY_NAME), name); }

    @org.omnetpp.common.properties.Property(category="Lines",id=PROP_DISPLAY_LINE,optional=true,
            description="Displays the line.")
    public boolean getDisplayLine() { return chartProps.getBooleanProperty(propertyName(PROP_DISPLAY_LINE)); }
    public void setDisplayLine(boolean display) { chartProps.setProperty(propertyName(PROP_DISPLAY_LINE), display); }
    public boolean defaultDisplayLine() { return ChartDefaults.DEFAULT_DISPLAY_LINE; }

    @org.omnetpp.common.properties.Property(category="Lines",id=PROP_SYMBOL_TYPE,optional=true,
            description="The symbol drawn at the data points.")
    public SymbolType getSymbolType() { return chartProps.getEnumProperty(propertyName(PROP_SYMBOL_TYPE), SymbolType.class); }
    public void setSymbolType(SymbolType type) { chartProps.setProperty(propertyName(PROP_SYMBOL_TYPE), type); }
    public SymbolType defaultSymbolType() { return null; }

    @org.omnetpp.common.properties.Property(category="Lines",id=PROP_SYMBOL_SIZE,
            description="The size of the symbol drawn at the data points.")
    public Integer getSymbolSize() { return chartProps.getIntegerProperty(propertyName(PROP_SYMBOL_SIZE), lineId==null); }
    public void setSymbolSize(Integer size) { chartProps.setProperty(propertyName(PROP_SYMBOL_SIZE), size); }
    public Integer defaultSymbolSize() { return lineId == null ? ChartDefaults.DEFAULT_SYMBOL_SIZE : null; }

    @org.omnetpp.common.properties.Property(category="Lines",id=PROP_LINE_TYPE,optional=true,
            description="Line drawing method. One of Linear, Pins, Dots, Points, Sample-Hold or Backward Sample-Hold.")
    public LineType getLineType() { return chartProps.getEnumProperty(propertyName(PROP_LINE_TYPE), LineType.class); }
    public void setLineType(LineType style) { chartProps.setProperty(propertyName(PROP_LINE_TYPE), style); }
    public LineType defaultLineType() { return null; }

    @org.omnetpp.common.properties.Property(category="Lines",id=PROP_LINE_COLOR,descriptorClass=ColorPropertyDescriptor.class,optional=true,
            description="Color of the line. Color name or #RRGGBB. Press Ctrl+Space for a list of color names.")
    public String getLineColor() { return chartProps.getStringProperty(propertyName(PROP_LINE_COLOR)); } // FIXME use RGB
    public void setLineColor(String color) { chartProps.setProperty(propertyName(PROP_LINE_COLOR), color); }
    public String defaultLineColor() { return null; }
}