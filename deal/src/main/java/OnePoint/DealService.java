package OnePoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author seoyeon on 2020/07/13
 * @project OnePoint
 */
@Service
public class DealService {

  @Autowired
  DealRepository dealRepository;

  /**
   * 가맹점 번호와 청구년월을 입력받아 정산금액을 리턴함
   *
   * @param mercharntId
   * @param billingMonth
   * @return
   * @throws ParseException
   */
  public Double billing(Long mercharntId, String billingMonth) throws ParseException {

    SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd");

    //1. 정산대상 선정
    Date startDate = transFormat.parse(billingMonth + "01");
    Date endDate = transFormat.parse(billingMonth + "31");

    List<Deal> billingListByMercharntIdAndBillngMont = dealRepository
        .findBillingListByMercharntIdAndBillngMont(mercharntId, startDate, endDate);

    Double sum = 0.0;

    for (int i = 0; i < billingListByMercharntIdAndBillngMont.size(); i++) {
      sum += billingListByMercharntIdAndBillngMont.get(i).getPoint();

    }
    //2. 정산 여부를 업데이쳐줌
    for (int j = 0; j < billingListByMercharntIdAndBillngMont.size(); j++) {
      billingListByMercharntIdAndBillngMont.get(j).setBillingStatus("yes");
      dealRepository.save(billingListByMercharntIdAndBillngMont.get(j));
    }
    return sum*0.98;
  }
}
