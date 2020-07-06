package OnePoint;

import OnePoint.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class BillingAmountViewViewHandler {

  static final double commission = 0.02;
  @Autowired
  private BillingAmountViewRepository billingAmountViewRepository;

    /**
     * Deal시스템이 사용거래 (success)가 발생한 경우 billingAmountView에 데이터가 적재
     * 데이터 적재 시, 정산금앨을 계산하여 입력함
     * @param useRequested
     */
  @StreamListener(KafkaProcessor.INPUT)
  public void whenUseRequested_then_CREATE_1(@Payload UseRequested useRequested) {
    try {
      if (useRequested.isMe()) {

        // view 객체 생성
        BillingAmountView billingAmountView = new BillingAmountView();

        // view 객체에 이벤트의 Value 를 set 함
        billingAmountView.setId(useRequested.getId());
        billingAmountView.setMerchantId(useRequested.getMerchantId());
        billingAmountView.setDealDate(useRequested.getDealDate());
        billingAmountView.setType(useRequested.getType());
        billingAmountView.setPoint(useRequested.getPoint());

        //수수료를 뗀 정산금액을 계산해서 넣어줌 : 사용자가 쓴 포인트 x 0.98을 가맹점에 지급해야 함
        billingAmountView.setBillingAmount(useRequested.getPoint() * (1 - commission));

        // view 레파지 토리에 save
        billingAmountViewRepository.save(billingAmountView);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}