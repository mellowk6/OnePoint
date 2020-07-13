package OnePoint.handeler;

import OnePoint.Model.Event.MemberCreated;
import OnePoint.Model.Event.MemberSecession;
import OnePoint.Model.Event.SavedDealCancelled;
import OnePoint.Model.Event.UsedDealCancelled;
import OnePoint.Point;
import OnePoint.config.kafka.KafkaProcessor;
import OnePoint.repostory.PointRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler {

  @Autowired
  PointRepository pointRepository;

  @StreamListener(KafkaProcessor.INPUT)
  public void wheneverMemberSecession_PointDisappeared(@Payload MemberSecession memberSecession) {

    if (memberSecession.isMe()) {

      System.out.println("##### listener PointDisappeared : " + memberSecession.toJson());
    }
  }

  @StreamListener(KafkaProcessor.INPUT)
  public void wheneverMemberCreated_MemberCreated(@Payload MemberCreated memberCreated) {

    if (memberCreated.isMe()) {

      Point point = new Point();
      point.setMemberId(memberCreated.getMemberId());
      point.setPoint(0.0);

      pointRepository.save(point);

      System.out.println("##### listener MemberCreated : " + memberCreated.toJson());
    }
  }

  @StreamListener(KafkaProcessor.INPUT)
  public void wheneverMemberCreated_SaveCancelCreated(
      @Payload SavedDealCancelled savedDealCancelled) { // 적립거래 취소 : 포인트 감소

    if (savedDealCancelled.isMe()) {

        Point point = new Point();
        point.setMemberId(savedDealCancelled.getMemberId());
        Optional<Point> pointBymemberId = pointRepository.findById(point.getMemberId());
        point.setPoint(pointBymemberId.get().getPoint()-savedDealCancelled.getPoint());

        pointRepository.save(point);


    }
  }

  @StreamListener(KafkaProcessor.INPUT)
  public void wheneverMemberCreated_UseCancelCreated(@Payload UsedDealCancelled usedDealCancelled) { //사용거래 취소 : 포인트 증가

    if (usedDealCancelled.isMe()) {

        Point point = new Point();
        point.setMemberId(usedDealCancelled.getMemberId());
        Optional<Point> pointBymemberId = pointRepository.findById(point.getMemberId());
        point.setPoint(usedDealCancelled.getPoint()+pointBymemberId.get().getPoint());

        pointRepository.save(point);
    }

  }

}
