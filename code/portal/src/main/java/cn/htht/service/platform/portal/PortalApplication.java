package cn.htht.service.platform.portal;

import cn.htht.service.platform.portal.utils.utils.file.FileMD5Utils;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication(exclude = PageHelperAutoConfiguration.class)
public class PortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }

}
