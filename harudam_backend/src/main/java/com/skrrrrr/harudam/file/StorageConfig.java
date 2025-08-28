package com.skrrrrr.harudam.file;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 파일 저장소 관련 설정을 활성화하는 Config 클래스.
 * FileStorageProperties 를 Bean 으로 등록해주어야
 * 다른 서비스에서 @Autowired 로 사용 가능하다.
 */
@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class StorageConfig {
    // 별도의 Bean 정의 없이 Properties 클래스만 활성화한다.
}
