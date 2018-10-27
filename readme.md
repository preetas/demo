# Handling jsp resources into fat jar with Spring Boot and embedded Jetty

Hello!

This article about handling static jsp resources into fat jar with Spring boot and embedded Jetty.

Firstly, you should create Spring Boot project. [SPRING INITIALIZR](http://start.spring.io) is convenient service to create project. Select maven project with java and spring boot 2.0.2 and add spring boot starter web dependency. And click to generate project

Further,  open new project in your favorite IDE.

I use embedded Jetty as Java Servlet Container which ships with spring boot starter web. Jetty require some dependencies to render jsp view. I use tomcat-embed-jasper library. It is more fast than Jetty's one. Add this dependencies to pom.xml:

   		<!-- JSP support -->
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-jasper</artifactId>
		</dependency>
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>jstl</artifactId>
		</dependency>

I am going to use fat jar and I would like Jetty handle correctly my jsp files. So I have to put *.jsp files into `${project.basedir}/src/main/resources/META-INF/resources/WEB-INF`

Here is example hello.jsp file. It just print "Hello world!"

    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <html>
    <head>
        <title>Home</title>
    </head>
    <body>
        <h1>Hello world!</h1>
    </body>
    </html>

Next, add view controller with `hello` request mapping which return `hello` view name.

    @SpringBootApplication
    public class SpringBootJspFatJarJettyApplication implements WebMvcConfigurer {
      // other class method is omitted

      @Override
      public void addViewControllers(ViewControllerRegistry registry) {
          registry.addViewController("/hello").setViewName("hello");
       }
    }

And add some properties in application.yml to resolve view name correctly:

    spring:
      mvc:
        view:
          prefix: WEB-INF/
          suffix: .jsp

If you run project with IDE it would run fine. But if you run project packaged as jar file you get 404 error. How to fix it? I spent long hour to resolve this issue. I have found one way.

I create class JettyJspConfiguration which extend org.eclipse.jetty.webapp.AbstractConfiguration and override preConfigure() method as follow:
```
@Component
public class JettyJspConfiguration extends AbstractConfiguration {

    @Override
    public void preConfigure(WebAppContext context) throws Exception {
        context.setResourceBase(getJspResourceUrl());
    }

    private String getJspResourceUrl() throws IOException {
        return new ClassPathResource("META-INF/resources/").getURI().toASCIIString();
    }
}
```
When Jetty will use this configuration it will set ResourceBase url to "META-INF/resources/" in classpath. Therefore, jsp files will be accessible in fat jar file.

After that I add configuration using JettyServletWebServerFactory as follow:

```
@Configuration
public class JettyConfig {

    @Bean
    public ServletWebServerFactory servletWebServerFactory(JettyJspConfiguration jspConfiguration) {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.addConfigurations(jspConfiguration);
        return factory;
    }
}
```

Finally, you run project packaged as jar you watch `Hello World!` in your browser.

Complete source code you can find in [DarrMirr/jsp-in-jar](https://github.com/DarrMirr/spring-boot-jsp-fat-jar-jetty)
