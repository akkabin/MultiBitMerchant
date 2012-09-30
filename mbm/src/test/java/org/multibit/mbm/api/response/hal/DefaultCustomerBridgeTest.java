package org.multibit.mbm.api.response.hal;

import com.google.common.base.Optional;
import com.theoryinpractise.halbuilder.spi.Resource;
import org.junit.Before;
import org.junit.Test;
import org.multibit.mbm.db.DatabaseLoader;
import org.multibit.mbm.db.dto.*;
import org.multibit.mbm.test.BaseResourceTest;
import org.multibit.mbm.test.FixtureAsserts;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

public class DefaultCustomerBridgeTest extends BaseResourceTest {

  @Before
  public void setUpDefaultRequestInfo() throws Exception {
    setUpUriInfo(Optional.<String>absent());
    setUpHttpHeaders(Optional.<List<MediaType>>absent());
  }


  @Test
  public void representCustomerAsJson() throws Exception {

    Role customerRole = DatabaseLoader.buildCustomerRole();
    User aliceUser = DatabaseLoader.buildAliceCustomer(customerRole);

    DefaultCustomerBridge testObject = new DefaultCustomerBridge(uriInfo,principal);

    Resource resource = testObject.toResource(aliceUser.getCustomer());

    FixtureAsserts.assertRepresentationMatchesJsonFixture("a Customer can be marshalled to JSON", resource, "fixtures/hal/customer/expected-customer-simple.json");

  }

  @Test
  public void representCustomerAsXml() throws IOException {

    Role customerRole = DatabaseLoader.buildCustomerRole();
    User aliceUser = DatabaseLoader.buildAliceCustomer(customerRole);

    DefaultCustomerBridge testObject = new DefaultCustomerBridge(uriInfo,principal);

    Resource resource = testObject.toResource(aliceUser.getCustomer());

    FixtureAsserts.assertRepresentationMatchesXmlFixture("a Customer can be marshalled to XML", resource, "fixtures/hal/customer/expected-customer-simple.xml");

  }

  private Customer buildCustomer() {
    User customerAlice = DatabaseLoader.buildAliceCustomer(null);
    customerAlice.setId(1L);

    return CustomerBuilder
      .newInstance()
      .build();
  }


}
