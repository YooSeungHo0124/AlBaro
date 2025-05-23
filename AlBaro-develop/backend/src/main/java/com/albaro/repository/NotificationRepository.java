package com.albaro.repository;

import com.albaro.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>  {
    List<Notification> findByStore_StoreId(Integer storeId);
}
