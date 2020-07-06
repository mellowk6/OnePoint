package OnePoint.controller;

import OnePoint.Point;
import OnePoint.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class PointController {

  @Autowired
  PointService pointService;

  /**
   * @param pointDecreaseRequest
   * @return 포인트가 사용할 만큼 충분한 경우, 회원 포인트를 감소시키고 true를 리턴
   * @throws Exception
   */
  @PostMapping("/pointDecrease")
  public String pointDecrease(@RequestBody PointDecreaseRequest pointDecreaseRequest)
      throws Exception {

    Point point = new Point();

    point.setMemberId(pointDecreaseRequest.getMemberId());
    point.setPoint(pointDecreaseRequest.getPoint());

    try {
      if (!pointService.pointDecrease(point)) {
        throw new PointLackException();
      }
    } catch (PointLackException e) {
      e.printStackTrace();
      System.err.println("PointLack Exception이 발생했습니다.");
      return "false";
    }
    return "true";
  }

  /**
   *
   * @param pointIncreaseRequest
   */
  @PostMapping("/pointIncrease")
  public void pointIncrease(@RequestBody PointIncreaseRequest pointIncreaseRequest)
      throws Exception {

    Point point = new Point();
    point.setMemberId(pointIncreaseRequest.getMemberId());
    point.setPoint(pointIncreaseRequest.getPoint());

    pointService.pointIncrease(point);
  }


}
