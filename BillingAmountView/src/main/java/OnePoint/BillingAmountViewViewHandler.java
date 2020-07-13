package OnePoint;

import OnePoint.config.kafka.KafkaProcessor;
import java.util.List;
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
   * Deal시스템이 사용거래가 발생한 경우 billingAmountView에 데이터가 적재
   *
   * @param useRequested
   */
  @StreamListener(KafkaProcessor.INPUT)
  public void whenUseRequested_then_CREATE_1(@Payload UseRequested useRequested) {
    try {
      if (useRequested.isMe()) {
        System.out.println("VillingAmount useRequest!!!!");


        // view 객체 생성
        BillingAmountView billingAmountView = new BillingAmountView();

        // view 객체에 이벤트의 Value 를 set 함
        billingAmountView.setId(useRequested.getId());
        billingAmountView.setMerchantId(useRequested.getMerchantId());
        billingAmountView.setDealDate(useRequested.getDealDate());
        billingAmountView.setType(useRequested.getType());
        billingAmountView.setPoint(useRequested.getPoint());
        billingAmountView.setMemberId(useRequested.getMemberId());
        billingAmountView.setBillingstatus(useRequested.getBillingStatus());
        billingAmountView.setMemberStatus("valid");

        //수수료를 뗀 정산금액을 계산해서 넣어줌 : 사용자가 쓴 포인트 x 0.98을 가맹점에 지급해야 함
        //billingAmountView.setBillingAmount(useRequested.getPoint() * (1 - commission));

        // view 레파지 토리에 save
        billingAmountViewRepository.save(billingAmountView);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @StreamListener(KafkaProcessor.INPUT)
  public void whenSaveDealt_then_CREATE_1(@Payload SaveDealt saveDealt) {
    try {
      if (saveDealt.isMe()) {

        // view 객체 생성
        BillingAmountView billingAmountView = new BillingAmountView();

        // view 객체에 이벤트의 Value 를 set 함
        billingAmountView.setId(saveDealt.getId());
        billingAmountView.setMerchantId(saveDealt.getMerchantId());
        billingAmountView.setDealDate(saveDealt.getDealDate());
        billingAmountView.setType(saveDealt.getType());
        billingAmountView.setPoint(saveDealt.getPoint());
        billingAmountView.setMemberId(saveDealt.getMemberId());
        billingAmountView.setBillingstatus(saveDealt.getBillingStatus());
        billingAmountView.setMemberStatus("valid");

        // view 레파지 토리에 save
        billingAmountViewRepository.save(billingAmountView);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @StreamListener(KafkaProcessor.INPUT)
  public void whenUsedDealCancelled_then_CREATE_1(@Payload UsedDealCancelled usedDealCancelled) {
    try {
      if (usedDealCancelled.isMe()) {

        // view 객체 생성
        BillingAmountView billingAmountView = new BillingAmountView();

        // view 객체에 이벤트의 Value 를 set 함
        billingAmountView.setId(usedDealCancelled.getId());
        billingAmountView.setMerchantId(usedDealCancelled.getMerchantId());
        billingAmountView.setDealDate(usedDealCancelled.getDealDate());
        billingAmountView.setType(usedDealCancelled.getType());
        billingAmountView.setPoint(usedDealCancelled.getPoint());
        billingAmountView.setMemberId(usedDealCancelled.getMemberId());
        billingAmountView.setBillingstatus(usedDealCancelled.getBillingStatus());
        billingAmountView.setMemberStatus("valid");

        // view 레파지 토리에 save
        billingAmountViewRepository.save(billingAmountView);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @StreamListener(KafkaProcessor.INPUT)
  public void whenSavedDealCancelled_then_CREATE_1(@Payload SavedDealCancelled savedDealCancelled) {
    try {
      if (savedDealCancelled.isMe()) {

        // view 객체 생성
        BillingAmountView billingAmountView = new BillingAmountView();

        // view 객체에 이벤트의 Value 를 set 함
        billingAmountView.setId(savedDealCancelled.getId());
        billingAmountView.setMerchantId(savedDealCancelled.getMerchantId());
        billingAmountView.setDealDate(savedDealCancelled.getDealDate());
        billingAmountView.setType(savedDealCancelled.getType());
        billingAmountView.setPoint(savedDealCancelled.getPoint());
        billingAmountView.setMemberId(savedDealCancelled.getMemberId());
        billingAmountView.setBillingstatus(savedDealCancelled.getBillingStatus());
        billingAmountView.setMemberStatus("valid");

        // view 레파지 토리에 save
        billingAmountViewRepository.save(billingAmountView);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @StreamListener(KafkaProcessor.INPUT)
  public void whenMemberSecession_then_CREATE_1(@Payload MemberSecession memberSecession) {
    try {
      if (memberSecession.isMe()) {
        System.out.println("여기 들어옴 ??? ");
        // view 객체 생성
        //BillingAmountView billingAmountView = new BillingAmountView();

        List<BillingAmountView> list = billingAmountViewRepository
            .findByMemberId(memberSecession.getMemberId());
        Double sum = 0.0;

        //2. 정산 여부를 업데이쳐줌
        for (int j = 0; j < list.size(); j++) {
          System.out.println("###test : "+j);
          list.get(j).setMemberStatus("invalid");
          billingAmountViewRepository.save(list.get(j));
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}