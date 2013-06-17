/**
 * Copyright (c) 2013, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package impl.org.controlsfx.skin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.control.PropertySheet.Mode;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.property.editor.ObjectViewer;
import org.controlsfx.property.editor.PropertyEditor;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class PropertySheetSkin extends BehaviorSkinBase<PropertySheet, BehaviorBase<PropertySheet>> {

    /**************************************************************************
     * 
     * Static fields
     * 
     **************************************************************************/

    private static final int MIN_COLUMN_WIDTH = 100;
    
    /**************************************************************************
     * 
     * fields
     * 
     **************************************************************************/
    
    private final BorderPane content = new BorderPane();
    private final ScrollPane scroller = new ScrollPane();
    private final ToolBar toolbar = new ToolBar();
    private final SegmentedButton modeButton = ActionUtils.createSegmentedButton(
        new ActionChangeMode(Mode.NAME),
        new ActionChangeMode(Mode.CATEGORY)
    );
    private final TextField searchField = new TextField();
    
    
    /**************************************************************************
     * 
     * Constructors
     * 
     **************************************************************************/

    public PropertySheetSkin(final PropertySheet control) {
        super(control, new BehaviorBase<>(control));
        
        scroller.setFitToWidth(true);
        
        toolbar.managedProperty().bind(toolbar.visibleProperty());
        
       // property sheet mode
        modeButton.managedProperty().bind(modeButton.visibleProperty());
        modeButton.getButtons().get(getSkinnable().modeProperty().get().ordinal()).setSelected(true);
        toolbar.getItems().add(modeButton);
        
        // property sheet search
        searchField.setPromptText("Search");
        searchField.managedProperty().bind(searchField.visibleProperty());
        toolbar.getItems().add(searchField);
        
        // layout controls
        content.setTop(toolbar);
        content.setCenter(scroller);
        getChildren().add(content);
              
        
        // setup listeners
        registerChangeListener(control.modeProperty(), "MODE");
        registerChangeListener(control.propertyEditorFactory(), "EDITOR-FACTORY");
        registerChangeListener(control.titleFilter(), "FILTER");
        registerChangeListener(searchField.textProperty(), "FILTER-UI");
        registerChangeListener(control.toolbarVisibleProperty(), "TOOLBAR");
        registerChangeListener(control.toolbarModeVisibleProperty(), "TOOLBAR-MODE");
        registerChangeListener(control.toolbarSearchVisibleProperty(), "TOOLBAR-SEARCH");
        
        control.getItems().addListener( new ListChangeListener<Item>() {
            @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends Item> change) {
                refreshProperties();
            }
        });
        
        
    }


    /**************************************************************************
     * 
     * Overriding public API
     * 
     **************************************************************************/

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if (p == "MODE" || p == "EDITOR-FACTORY" || p == "FILTER") {
            refreshProperties();
        }
        if (p == "FILTER-UI") {
            getSkinnable().setTitleFilter(searchField.getText());
        }
        if (p == "TOOLBAR") {
            toolbar.setVisible(getSkinnable().isToolbarVisible());
        }
        if (p == "TOOLBAR-MODE") {
            modeButton.setVisible(getSkinnable().isToolbarModeVisible());
        }
        if (p == "TOOLBAR-SEARCH") {
            searchField.setVisible(getSkinnable().isToolbarSearchVisible());
        }
    }
    
    @Override protected void layoutChildren(double x, double y, double w, double h) {
        content.resizeRelocate(x, y, w, h);
    }



    /**************************************************************************
     * 
     * Implementation
     * 
     **************************************************************************/

    private void refreshProperties() {
        scroller.setContent(buildPropertySheetContainer());
    }
    
    private Node buildPropertySheetContainer() {
        switch( getSkinnable().modeProperty().get() ) {

            case CATEGORY: {
                
                // group by category
                Map<String, List<Item>> categoryMap = new TreeMap<>();
                for( Item p: getSkinnable().getItems()) {
                    String category = p.getCategory();
                    List<Item> list = categoryMap.get(category);
                    if ( list == null ) {
                        list = new ArrayList<>();
                        categoryMap.put( category, list);
                    }
                    list.add(p);
                }
                
                // create category-based accordion
                Accordion accordeon = new Accordion();
                for( String category: categoryMap.keySet() ) {
                    TitledPane pane = new TitledPane( category, new PropertyPane( categoryMap.get(category)));
                    pane.setExpanded(true);
                    accordeon.getPanes().add(pane);
                }
                if ( accordeon.getPanes().size() > 0 ) {
                    accordeon.setExpandedPane( accordeon.getPanes().get(0));
                }
                return accordeon;
            }
            
            default: return new PropertyPane(getSkinnable().getItems());
        }
        
    }

    private String capitalize( String s ) {
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
    
    /**************************************************************************
     * 
     * Support classes / enums
     * 
     **************************************************************************/
    
    private class ActionChangeMode extends AbstractAction {
        
        private PropertySheet.Mode mode;
        
        public ActionChangeMode( PropertySheet.Mode mode ) {
            super("By " + capitalize(mode.toString()));
            this.mode = mode;
        }

        @Override public void execute(ActionEvent ae) {
            getSkinnable().modeProperty().set(mode);
        }
        
        
    }
    
    
    private class PropertyPane extends GridPane {
        
        public PropertyPane( List<Item> properties ) {
            this( properties, 0 );
        }
        
        public PropertyPane( List<Item> properties, int nestingLevel ) {
            setVgap(5);
            setHgap(5);
            setPadding(new Insets(5, 15, 5, 15 + nestingLevel*10 ));
            getStyleClass().add("property-pane");
            setItems(properties);
//            setGridLinesVisible(true);
        }
        
        public void setItems( List<Item> properties ) {
            getChildren().clear();
            
            String filter = getSkinnable().titleFilter().get();
            filter = filter == null? "": filter.trim().toLowerCase();

            int row = 0;
            
            for (Item item : getSkinnable().getItems()) {

                // filter properties
                String title = item.getName();
               
                if ( !filter.isEmpty() && title.toLowerCase().indexOf( filter ) < 0) continue;
                
                // setup property label
                Label label = new Label(title);
                label.setMinWidth(MIN_COLUMN_WIDTH);
                
                // show description as a tooltip
                String description = item.getDescription();
                if ( description != null && !description.trim().isEmpty()) {
                    label.setTooltip( new Tooltip(description));
                }
                
                add(label, 0, row);

             // setup property editor
                Region editor = getEditor(item);
                editor.setMinWidth(MIN_COLUMN_WIDTH);
                editor.setMaxWidth(Double.MAX_VALUE);
                label.setLabelFor(editor);
                add(editor, 1, row);
                GridPane.setHgrow(editor, Priority.ALWAYS);
                
              //TODO add support for recursive properties
                
                row++;
                
            }
        }
        
        private Region getEditor( Item item ) {
            
            PropertyEditor editor = getSkinnable().getPropertyEditorFactory().getEditor(item);
            if ( editor == null ) {
                editor = new ObjectViewer(item);
            }
            editor.setValue(item.getValue());
            return editor.asNode();
            
        }
        
        
    }

}