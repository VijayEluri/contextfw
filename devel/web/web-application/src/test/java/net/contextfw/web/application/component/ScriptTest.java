package net.contextfw.web.application.component;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;

public class ScriptTest extends BaseComponentTest {

	private Gson gson = new Gson();
	
	@Test
	public void test1() {
		Script script = new Script("foo()", null);
		script.build(domBuilder.descend("Script"), gson, componentBuilder);
		assertDom("//WebApplication/Script").hasText("foo()");
	}
	
	@Test
	public void test2() {
		Script script = new Script("foo({0})", new Object[] {true});
		script.build(domBuilder.descend("Script"), gson, componentBuilder);
		assertDom("//WebApplication/Script").hasText("foo(true)");
	}
	
	@Test
	public void test3() {
		List<String> strs = new ArrayList<String>();
		strs.add("1"); strs.add("2");
		Script script = new Script("foo({0}, {1})", new Object[] {strs, "3"});
		script.build(domBuilder.descend("Script"), gson, componentBuilder);
		assertDom("//WebApplication/Script").hasText("foo([\"1\",\"2\"], \"3\")");
	}
	
	@Test
	public void test4() {
		List<String> strs = new ArrayList<String>();
		strs.add("1"); strs.add("2");
		Function script = new Function("foo", strs, "3");
		script.build(domBuilder.descend("Script"), gson, componentBuilder);
		assertDom("//WebApplication/Script").hasText("foo([\"1\",\"2\"],\"3\");\n");
	}
	
	@Test
	public void test5() {
		List<String> strs = new ArrayList<String>();
		strs.add("1"); strs.add("2");
		Function script = new Function("foo");
		script.build(domBuilder.descend("Script"), gson, componentBuilder);
		assertDom("//WebApplication/Script").hasText("foo();\n");
	}
	
	@Test
	public void test6() {
		List<String> strs = new ArrayList<String>();
		strs.add("1"); strs.add("2");
		A a = new A();
		a.setId("el1");
		ComponentFunction script = new ComponentFunction(a, "foo");
		script.build(domBuilder.descend("Script"), gson, componentBuilder);
		assertDom("//WebApplication/Script").hasText("A(\"el1\").foo();\n");
	}
	
	@Test
	public void test7() {
		List<String> strs = new ArrayList<String>();
		strs.add("1"); strs.add("2");
		A a = new A();
		a.setId("el1");
		ComponentFunction script = new ComponentFunction(a, "foo", 1, 2);
		script.build(domBuilder.descend("Script"), gson, componentBuilder);
		assertDom("//WebApplication/Script").hasText("A(\"el1\").foo(1,2);\n");
	}
	
	public static class A extends Component {}
}
