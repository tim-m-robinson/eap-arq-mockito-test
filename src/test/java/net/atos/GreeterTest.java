package net.atos;

import java.io.File;
import javax.inject.Inject;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(Arquillian.class)
public class GreeterTest {

  @Deployment
  public static Archive<?> createDeployment() {
    File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                    .resolve("io.rest-assured:rest-assured",
                             "org.mockito:mockito-core")
                    .withTransitivity().asFile();

    Archive war = ShrinkWrap.create(WebArchive.class, "test.war")
                    .addClass(Application.class)
                    .addClass(Greeter.class)
                    .addAsLibraries(libs)
                    .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    System.out.println(war.toString(true));
    return war;
  }

  @Inject
  Greeter greeter;

  /***
   * Test using the real injected test bean
   */
  @Test
  @InSequence(1)
  public void should_return_real_hello_world() {
    Assert.assertEquals("Hello World!", greeter.createGreeting(null).getEntity());
  }

  /***
   * Test using the mocked test bean
   */
  @Test
  @InSequence(2)
  public void should_return_mock_hello_world() {
    // Create Mock objects
    Greeter mockGreeter = mock(Greeter.class);
    Response mockResponse = mock(Response.class);
    // Set up Mock behaviour
    when(mockResponse.getEntity()).thenReturn("mock");
    when(mockGreeter.createGreeting(anyString())).thenReturn(mockResponse);

    Assert.assertEquals("mock", mockGreeter.createGreeting("anything").getEntity());
  }

}