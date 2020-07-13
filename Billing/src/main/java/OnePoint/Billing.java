package OnePoint;

import OnePoint.external.DealService;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "Billing_table")
public class Billing {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Long mercharntId;
  private Double billingAmount;
  private String billingMonth;

  /**
   * billing이 생성 될 때 가맹점 번호와 청구년월을 입력 받아 billingAmountView에서 정산금액을 조회해옴
   */
  @PrePersist
  public void onPrePersist() {

    OnePoint.external.Deal deal = new OnePoint.external.Deal();

    //1.input값 셋팅
    deal.setMercharntId(this.getMercharntId());
    deal.setBillingMonth(this.getBillingMonth());

    Double billedAmount = Application.applicationContext.getBean(DealService.class)
        .billingAmount(deal);

    System.out.println("# 가맹점 : " + this.getMercharntId());
    System.out.println("# 청구월 : " + this.getBillingMonth());
    System.out.println("# 정산금액 : " + billedAmount);

    this.setBillingAmount(billedAmount);

  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMercharntId() {
    return mercharntId;
  }

  public void setMercharntId(Long mercharntId) {
    this.mercharntId = mercharntId;
  }

  public Double getBillingAmount() {
    return billingAmount;
  }

  public void setBillingAmount(Double billingAmount) {
    this.billingAmount = billingAmount;
  }


  public String getBillingMonth() {
    return billingMonth;
  }

  public void setBillingMonth(String billingMonth) {
    this.billingMonth = billingMonth;
  }
}
