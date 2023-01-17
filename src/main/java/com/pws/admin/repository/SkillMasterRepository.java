package com.pws.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pws.admin.entity.SkillMastertable;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillMasterRepository extends JpaRepository<SkillMastertable, Integer> {

   
    
   
}