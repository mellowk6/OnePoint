package OnePoint;

import OnePoint.Model.Event.PointCreated;
import OnePoint.Model.Event.PointDisappeared;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "Point_table")
public class Point {

  @Id
  private Long memberId;
  private Double point;

  @PostPersist
  public void onPostPersist() {

    PointCreated pointCreated = new PointCreated();
    BeanUtils.copyProperties(this, pointCreated);
    pointCreated.publishAfterCommit();
  }

  @PreRemove
  public void onPreRemove() {
    PointDisappeared pointDisappeared = new PointDisappeared();
    BeanUtils.copyProperties(this, pointDisappeared);
    pointDisappeared.publishAfterCommit();
  }


  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public Double getPoint() {
    return point;
  }

  public void setPoint(Double point) {
    this.point = point;
  }
}
