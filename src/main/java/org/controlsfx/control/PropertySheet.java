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
package org.controlsfx.control;

import impl.org.controlsfx.skin.PropertySheetSkin;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditorFactory;

public class PropertySheet extends Control {
    
    private final ObservableList<Item> properties = FXCollections.observableArrayList();
    
    public PropertySheet() {
        getStyleClass().add("property-sheet");
        modeProperty.set(Mode.NAME);
    }
    
    @Override protected Skin<?> createDefaultSkin() {
        return new PropertySheetSkin(this);
    }
    
    public ObservableList<Item> getItems() {
        return properties;
    }
    
    public enum Mode {
        NAME,
        CATEGORY
    }
    
    
    public static interface Item {
        
        Class<?> getType();
        
        String getCategory();
       
        String getName();
        
        String getDescription();
        
        Object getValue();
        
        void setValue( Object value );
       
   }
    
    // modeProperty 
    private final SimpleObjectProperty<Mode> modeProperty = new SimpleObjectProperty<>();
    
    public final SimpleObjectProperty<Mode> modeProperty() {
    	return modeProperty;
    }
    
    public Mode getMode() {
        return modeProperty.get();
    }
    
    public void setMode(Mode mode) {
        modeProperty.set(mode);
    }
  

    //propertyEditorFactory
    private final SimpleObjectProperty<PropertyEditorFactory> propertyEditorFactory = 
            new SimpleObjectProperty<PropertyEditorFactory>( new DefaultPropertyEditorFactory());
    
    public final SimpleObjectProperty<PropertyEditorFactory> propertyEditorFactory() {
        return propertyEditorFactory;
    }
    
    public final PropertyEditorFactory getPropertyEditorFactory() {
        return propertyEditorFactory.get();
    }
    
    public final void setPropertyEditorFactory( PropertyEditorFactory factory ) {
        propertyEditorFactory.set( factory == null? new DefaultPropertyEditorFactory(): factory );
    }
    
    //toolbarVisibleProperty
    private final SimpleBooleanProperty toolbarVisibleProperty = new SimpleBooleanProperty(true);
    
    public final SimpleBooleanProperty toolbarVisibleProperty() {
        return toolbarVisibleProperty;
    }
    
    public boolean isToolbarVisible() {
        return toolbarVisibleProperty.get();
    }
    
    public void setToolbarVisible( boolean visible ) {
        toolbarVisibleProperty.set(visible);
    }
    
    //toolbarModeVisibleProperty
    private final SimpleBooleanProperty toolbarModeVisibleProperty = new SimpleBooleanProperty(true);
    
    public final SimpleBooleanProperty toolbarModeVisibleProperty() {
        return toolbarModeVisibleProperty;
    }
    
    public boolean isToolbarModeVisible() {
        return toolbarModeVisibleProperty.get();
    }
    
    public void setToolbarModeVisible( boolean visible ) {
        toolbarModeVisibleProperty.set(visible);
    }
    
    //toolbarSearchVisibleProperty
    private final SimpleBooleanProperty toolbarSearchVisibleProperty = new SimpleBooleanProperty(true);
    
    public final SimpleBooleanProperty toolbarSearchVisibleProperty() {
        return toolbarSearchVisibleProperty;
    }
    
    public boolean isToolbarSearchVisible() {
        return toolbarSearchVisibleProperty.get();
    }
    
    public void setToolbarSearchVisible( boolean visible ) {
        toolbarSearchVisibleProperty.set(visible);
    }   
    
    //titleFilterProperty
    private final SimpleStringProperty titleFilterProperty  = new SimpleStringProperty("");
    
    public final SimpleStringProperty titleFilter() {
        return titleFilterProperty;
    }
    
    public String getTitleFilter() {
        return titleFilterProperty.get();
    }
    
    public void setTitleFilter( String filter ) {
        titleFilterProperty.set(filter);
    }
    
    @Override protected String getUserAgentStylesheet() {
        return getClass().getResource("propertysheet.css").toExternalForm();
    }
    
}