package OnePoint;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface DealRepository extends PagingAndSortingRepository<Deal, Long>{


  @Query("select b from Deal b where b.merchantId= :merchantId and b.dealDate >= :startDate and b.dealDate <= :endDate and b.billingStatus ='no' and b.type='use' and b.status='success'")
  List<Deal> findBillingListByMercharntIdAndBillngMont(
      @Param("merchantId") Long merchantId, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

}