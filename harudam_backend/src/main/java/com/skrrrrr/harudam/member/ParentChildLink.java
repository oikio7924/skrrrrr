package com.skrrrrr.harudam.member;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parent_child_link", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"parent_user_id", "child_user_id"}, name = "ux_pcl_parent_child")
})
@Getter
@Setter
public class ParentChildLink {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long linkId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_user_id", nullable = false)
	private ParentUser parentUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_user_id", nullable = false)
	private ChildUser childUser;
	
	@CreationTimestamp
	@Column(updatable = false)
	private ZonedDateTime createdAt;
}