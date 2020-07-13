package OnePoint;

import OnePoint.external.PointService;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;


@Entity
@Getter
@Setter
@Table(name = "Deal_table")
public class Deal {

  // @Autowired
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Long memberId;
  private Long merchantId;
  private Date dealDate;
  private Double point;
  private String type;
  private Double dealAmount;
  private String status; // 거래 성공여부
  private String billingStatus; //정산여부

  /**
   * 적립거래 발생 시 데이터 셋
   */
  public void setSaveDeal() {
    Date now = new Date();
    this.setDealDate(now);
    if (this.dealAmount != null) {
      this.setPoint(this.dealAmount * 0.01);
    }
    this.setStatus("success");
    this.setBillingStatus("no");
  }

  /**
   * 사용거래 발생 시 데이터 셋팅 , 포인트가 함께 들어옴
   */
  public void setUseDeal() {
    System.out.println("##사용 생성자로 들어옴");
    Date now = new Date();
    this.setDealDate(now);
    this.setStatus("success");
    this.setBillingStatus("no");
  }

  /**
   * 사용취소 거래 발생 시 , 데이터 셋팅
   */
  private void setUseCancelDeal() {
    Date now = new Date();
    this.setDealDate(now);
    this.setStatus("success");
    this.setBillingStatus("no");
  }

  /**
   * 적립취소거리 발생 시, 데이터 셋팅
   */
  private void setSaveCancelDeal() {
    Date now = new Date();
    this.setDealDate(now);
    this.setStatus("success");
    this.setBillingStatus("no");
  }


  /**
   * 적립거래/사용거래 발생 1. 적립거래 발생 시 Point System에 memberId, point 정보를 전달 2. 사용거래 발생 시 Point System에
   * memberId, point 정보를 전달 회원의 잔여Point 부족으로 응답값이 false가 올 경우 거래상태(status)를 fail로 셋팅
   */

  @PrePersist
  public void onPrePersist() {
    if (this.getType().equals("save")) { // 1. 적립 거래 발생
      setSaveDeal();
      OnePoint.external.Point point = new OnePoint.external.Point();
      //1.input값 셋팅
      point.setMemberId(this.getMemberId());
      point.setPoint(this.getPoint());

      Application.applicationContext.getBean(OnePoint.external.PointService.class)
          .pointIncrease(point);

    } else if (this.getType().equals("use")) { //2. 사용거래
      setUseDeal();
      OnePoint.external.Point point = new OnePoint.external.Point();
      point.setMemberId(this.getMemberId());
      point.setPoint(this.getPoint());

      String pointDecreaseResult = Application.applicationContext.getBean(PointService.class)
          .pointDecrease(point);

      if (pointDecreaseResult.equals("false")) {
        this.setStatus("fail");
        System.out.println("유효하지 않은 거래이기 떄문에 삭제되었습니다.");
      }
    }
  }


  /**
   * 1. 사용거래 success 발생 시 , BillingAmountView 전달을 위해 비동기 통신을 함
   */

  @PostPersist
  public void onPostPersist() {
    if (this.getType().equals("use") && this.getStatus().equals("success")) {

      // 사용거래, 성공 시 view 생성을 위해 이벤트 발행
      UseRequested useRequested = new UseRequested();
      useRequested.setId(this.getId());
      useRequested.setMerchantId(this.getMerchantId());
      useRequested.setDealDate(this.getDealDate());
      useRequested.setType(this.getType());
      useRequested.setPoint(this.getPoint());
      useRequested.setBillingStatus("no");

      BeanUtils.copyProperties(this, useRequested);
      useRequested.publishAfterCommit();
    } else if (this.getType().equals("save")) {

      SaveDealt saveDealt = new SaveDealt();
      saveDealt.setId(this.getId());
      saveDealt.setMerchantId(this.getMerchantId());
      saveDealt.setDealDate(this.getDealDate());
      saveDealt.setType(this.getType());
      saveDealt.setPoint(this.getPoint());
      saveDealt.setBillingStatus("no");


      BeanUtils.copyProperties(this, saveDealt);
      saveDealt.publishAfterCommit();


    } else if (this.getType().equals("saveCancel")) { //3. 적립거래취
      setSaveCancelDeal();
      SavedDealCancelled savedDealCancelled = new SavedDealCancelled();
      savedDealCancelled.setId(this.getId());
      savedDealCancelled.setMerchantId(this.getMerchantId());
      savedDealCancelled.setDealDate(this.getDealDate());
      savedDealCancelled.setType(this.getType());
      savedDealCancelled.setPoint(this.getPoint());
      savedDealCancelled.setBillingStatus("no");

      BeanUtils.copyProperties(this, savedDealCancelled);
      savedDealCancelled.publishAfterCommit();

      //
    } else if (this.getType().equals("useCancel")) { //4. 사용거래 취소
      setUseCancelDeal();

      //사용거래 취소 !! -> 만약 dealingstatus가 ... 아 그러면 동기호출을 해와야 되는데 잠깐 패쓰!

      UsedDealCancelled usedDealCancelled = new UsedDealCancelled();

      usedDealCancelled.setId(this.getId());
      usedDealCancelled.setMerchantId(this.getMerchantId());
      usedDealCancelled.setDealDate(this.getDealDate());
      usedDealCancelled.setType(this.getType());
      usedDealCancelled.setPoint(this.getPoint());
      usedDealCancelled.setBillingStatus("no");
      usedDealCancelled.setBillingStatus(this.getBillingStatus());


      BeanUtils.copyProperties(this, usedDealCancelled);
      usedDealCancelled.publishAfterCommit();
    }
  }
}
