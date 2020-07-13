package OnePoint.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//@FeignClient(name = "billingAmountView", url = "http://localhost:8083")
//@FeignClient(name = "billingAmountView", url = "http://BillingAmountView:8080")
@FeignClient(name = "deal", url = "http://localhost:8085")
public interface DealService {

  @RequestMapping(method = RequestMethod.POST, path = "/dealBillingAmount")
  @ResponseBody
  Double billingAmount(@RequestBody Deal deal);
}