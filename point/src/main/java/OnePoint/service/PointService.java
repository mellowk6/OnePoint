package OnePoint.service;

import OnePoint.Point;
import OnePoint.repostory.PointRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PointService {

  @Autowired
  PointRepository pointRepository;

  /**
   * 1. 포인트 감소 요청 시, 포인트가 있는지 먼저 조회하기
   * 2. 포인트가 있는 경우에만 포인트 감소
   * @param point
   * @return 포인트가 사용가능한 경우만 true 리턴
   */
  public boolean pointDecrease(Point point) {

    Optional<Point> finedPoint = pointRepository.findById(point.getMemberId());
    if (finedPoint.isPresent()) {
      if (finedPoint.get().getPoint() >= point.getPoint()) {
        Double decreasedPoint = finedPoint.get().getPoint() - point.getPoint();
        point.setPoint(decreasedPoint);
        pointRepository.save(point);
        return true;
      }
    }
    return false;
  }

  /**
   * 1. 포인트 증가
   * @param point
   */
  public void pointIncrease(Point point) {

    Optional<Point> finedPoint = pointRepository.findById(point.getMemberId());
    if (finedPoint.isPresent()) {
      Double IncreasedPoint = finedPoint.get().getPoint() + point.getPoint();
      System.out.println("finedPoint.get().getPoint() : "+finedPoint.get().getPoint() );
      System.out.println("point.getPoint() : "+ point.getPoint());
      point.setPoint(IncreasedPoint);
    }
    pointRepository.save(point);
  }

}