package kd.mgproj;

import kd.mgproj.domain.notice.entity.Notice;
import kd.mgproj.domain.notice.repository.NoticeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class NoticeTest {
  @Autowired
  private NoticeRepository noticeRepository;

//  @Test
//  void create_test() {
//
//    Notice notice = new Notice("내 프로젝트", "내 프로젝트 설명 내용", "고길동");
//    noticeRepository.save(notice);
//
//    notice = new Notice("내 프로젝트2", "내 프로젝트 설명 내용2", "홍길동");
//    noticeRepository.save(notice);
//
//    notice = new Notice("내 프로젝트3", "내 프로젝트 설명 내용3", "둘리");
//    noticeRepository.save(notice);
//  }

  @Test
  void list_test() {
    List<Notice> list = noticeRepository.findAllByOrderByCreatedAtDesc();

    for(Notice notice:list) {
      System.out.print("-> " + notice.getId() + ",");
      System.out.println( notice.getTitle()+ "," + notice.getContent() +  "," + notice.getName() + "," + notice.getCreatedAt());
    }
  }
}
