package com.likelionfinalproject1.Repository;

import com.likelionfinalproject1.Domain.Entity.Alarm;
import com.likelionfinalproject1.Domain.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm,Long> {
    Optional<Alarm> findByTargetIdAndUserIdAndText(Long postid, Long userid,String text);

    @Transactional
    @Modifying
        // Post(Entity)에 해당하는 데이터는 모두 삭제
    void deleteAllByPost(@Param("post") Post post);     // @Param("post") = post라는 이름으로 Post(Entity)가 넘어온다는 뜻
}
