package OnePoint;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * @author seoyeon on 2020/07/13
 * @project OnePoint
 */

@Getter
@Setter
public class SavedDealCancelled extends AbstractEvent {

  private Long id;
  private Long memberId;
  private Long merchantId;
  private Date dealDate;
  private Double point;
  private String type;
  private Double dealAmount;
  private String Status;
 // private String billingStatus; //정산여부

  public SavedDealCancelled() {
    super();
  }
}
