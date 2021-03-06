/**
 * Copyright 2010 Marko Lavikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.contextfw.web.application.internal.initializer;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.PageScoped;
import net.contextfw.web.application.lifecycle.View;

import org.junit.Test;

public class InitializerProviderTest {

    @PageScoped
    @View
    private static class A extends Component {
        
    }
    
    @PageScoped
    @View(parent=A.class)
    private static class B extends Component {
        
    }
    
    @PageScoped
    @View(parent=A.class)
    private static class NotComponent {
        
    }
    
    @PageScoped
    private static class NoViewAnnotation extends Component {
        
    }
    
    @View
    private static class NoPageScopeAnnotation extends Component {
        
    }
    
    private InitializerProvider provider = new InitializerProvider();
    
    @Test
    public void Get_Chain() {
        List<Class<? extends Component>> chain = provider.getInitializerChain(B.class);
        assertEquals(2, chain.size());
        assertEquals(A.class, chain.get(0));
        assertEquals(B.class, chain.get(1));
    }
    
    @Test(expected=WebApplicationException.class)
    public void Null_Component() {
        provider.getInitializerChain(null);
    }
    
    @Test(expected=WebApplicationException.class)
    public void Not_Component() {
        provider.getInitializerChain(NotComponent.class);
    }
    
    @Test(expected=WebApplicationException.class)
    public void No_View_Annotation() {
        provider.getInitializerChain(NoViewAnnotation.class);
    }
    
    @Test(expected=WebApplicationException.class)
    public void No_PageScoped_Annotation() {
        provider.getInitializerChain(NoPageScopeAnnotation.class);
    }
}
