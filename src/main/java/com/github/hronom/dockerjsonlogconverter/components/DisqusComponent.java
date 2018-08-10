package com.github.hronom.dockerjsonlogconverter.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.shared.ui.LoadMode;

@Tag("div")
@JavaScript(value = "frontend://disqus.js", loadMode = LoadMode.LAZY)
public class DisqusComponent extends Component implements HasSize, HasEnabled {
    public DisqusComponent() {
        setId("disqus_thread");
    }
}
