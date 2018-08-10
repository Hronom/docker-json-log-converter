package com.github.hronom.dockerjsonlogconverter.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;

@Tag("div")
public class DisqusComponent extends Component implements HasSize, HasEnabled {
    public DisqusComponent() {
        setId("disqus_thread");
        addAttachListener((ComponentEventListener<AttachEvent>) event -> UI.getCurrent().getPage().executeJavaScript(
            "/**\n" +
            "*  RECOMMENDED CONFIGURATION VARIABLES: EDIT AND UNCOMMENT THE SECTION BELOW TO INSERT DYNAMIC VALUES FROM YOUR PLATFORM OR CMS.\n" +
            "*  LEARN WHY DEFINING THESE VARIABLES IS IMPORTANT: https://disqus.com/admin/universalcode/#configuration-variables*/\n" +
            "/*\n" + "var disqus_config = function () {\n" +
            "this.page.url = PAGE_URL;  // Replace PAGE_URL with your page's canonical URL variable\n" +
            "this.page.identifier = PAGE_IDENTIFIER; // Replace PAGE_IDENTIFIER with your page's unique identifier variable\n" +
            "};\n" + "*/\n" + "(function() { // DON'T EDIT BELOW THIS LINE\n" +
            "var d = document, s = d.createElement('script');\n" +
            "s.src = 'https://docker-json-log-converter.disqus.com/embed.js';\n" +
            "s.setAttribute('data-timestamp', +new Date());\n" +
            "(d.head || d.body).appendChild(s);\n" + "})();"
        ));
    }
}
