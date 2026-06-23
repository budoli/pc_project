package kd.mgproj;

import kd.mgproj.domain.notice.dto.request.NoticeRequestDto;
import kd.mgproj.domain.notice.service.NoticeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NoticeServiceTest {

  @Autowired
  NoticeService noticeService;

  @Test
  void service_save_test() {
    NoticeRequestDto noticeRequestDto = new NoticeRequestDto("6월19일", "비가 오기 시작했다.", "고길동");

    noticeService.save(noticeRequestDto);

  }
}
