package OnePoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DealController {


  @Autowired
  DealService dealService;

  @PostMapping("/dealBillingAmount")
  public Double billingAmount(@RequestBody BillingRequest billingRequest)
      throws Exception {

    double billingAmount = dealService
        .billing(billingRequest.getMercharntId(), billingRequest.getBillingMonth());

    return billingAmount;
  }
}
