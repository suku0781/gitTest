-- member테이블 생성
-- CREATE TABLE `ksy`.`member` (
--   `userId` VARCHAR(8) NOT NULL,
--   `userPwd` VARCHAR(500) NOT NULL,
--   `userEmail` VARCHAR(50) NULL DEFAULT 'null',
--   `registerDate` DATETIME NULL DEFAULT now(),
--   `userImg` VARCHAR(50) NULL DEFAULT 'null',
--   `userPoint` INT(11) NULL DEFAULT '0',
--   PRIMARY KEY (`userId`),
--   UNIQUE INDEX `userEmail_UNIQUE` (`userEmail` ASC) VISIBLE);

-- 유저 아이디 중복 검사
select * from member where userId = ?;


-- uploadedfile 테이블 생성
CREATE TABLE `ksy`.`uploadedfile` (
  `no` INT NOT NULL AUTO_INCREMENT,
  `originalFileName` VARCHAR(50) NULL,
  `ext` VARCHAR(5) NULL,
  `newFileName` VARCHAR(60) NULL,
  `fileSize` INT NULL,
  PRIMARY KEY (`no`));
  
  
-- pointpolicy 테이블 생성
  CREATE TABLE `ksy`.`pointpolicy` (
  `why` VARCHAR(50) NOT NULL,
  `howmuch` INT NULL,
  PRIMARY KEY (`why`));
  
INSERT INTO `ksy`.`pointpolicy` (`why`, `howmuch`) VALUES ('회원가입', '100');
INSERT INTO `ksy`.`pointpolicy` (`why`, `howmuch`) VALUES ('로그인', '5');
INSERT INTO `ksy`.`pointpolicy` (`why`, `howmuch`) VALUES ('게시물작성', '2');
INSERT INTO `ksy`.`pointpolicy` (`why`, `howmuch`) VALUES ('답글작성', '1');  
  
-- pointlog 테이블 생성
CREATE TABLE `ksy`.`pointlog` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `when` DATETIME NOT NULL DEFAULT now(),
  `why` VARCHAR(50) NULL,
  `howmuch` INT NULL,
  `who` VARCHAR(8) NULL,
  PRIMARY KEY (`id`),
  INDEX `pointlog_why_fk_idx` (`why` ASC) VISIBLE,
  INDEX `pointlog_who_fk_idx` (`who` ASC) VISIBLE,
  CONSTRAINT `pointlog_why_fk`
    FOREIGN KEY (`why`)
    REFERENCES `ksy`.`pointpolicy` (`why`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `pointlog_who_fk`
    FOREIGN KEY (`who`)
    REFERENCES `ksy`.`member` (`userId`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);  
  
  
-- 업로드된 파일의 정보를 uploadedFile테이블에 insert
insert into uploadedfile(originalFileName, ext, newFileName, fileSize) values(?,?,?,?);

-- 현재 업로드된 파일의 저장 번호(no)를 select해와서 반환 
select no from uploadedfile where newFileName = ? ;

-- 회원 데이터 insert
insert into member(userId, userPwd,userEmail, userImg, userPoint) values (?, sha1(md5(?)), ?, ?, ?);

select sha1(md5('1234')); 

-- pointlog테이블에 회원가입 포인트 로그를 남겨야 함.
insert into pointlog(why, howmuch, who) values(?, ?, ?);

-- 로그인 검사 (dooly, 1234)
select * from member where userId = 'dooly' and userPwd = sha1(md5('1234'));

-- 멤버 수정
ALTER TABLE `ksy`.`member` 
ADD COLUMN `isAdmin` VARCHAR(1) NULL DEFAULT 'N' AFTER `userPoint`;

-- 회원정보 + memberImg 쿼리문
select m.*, u.newFileName 
from member m inner join uploadedfile u 
on m.userImg = u.no 
where userId = 'dooly' and userPwd = sha1(md5('1234'));

-- 로그인 성공시, member테이블 포인트 update
update member set userPoint =  userPoint + 5    where userId = 'dooly';
select * from member where userId = 'dooly';

-- 해당 아이디 회원의 정보
select m.*, u.newFileName 
from member m inner join uploadedfile u 
on m.userImg = u.no 
where userId = 'dooly';

-- 해당 회원의 포인트 내역 
select * from pointlog where who = 'dooly';

-- 게시판 테이블 생성
 CREATE TABLE `ksy`.`board` (
   `no` INT NOT NULL AUTO_INCREMENT,
   `writer` VARCHAR(8) NULL,
   `title` VARCHAR(100) NOT NULL,
   `postDate` DATETIME NULL DEFAULT now(),
   `content` VARCHAR(1000) NOT NULL,
   `readcount` INT NULL DEFAULT 0,
   `likecount` INT NULL DEFAULT 0,
   `ref` INT NULL DEFAULT NULL,
   `step` INT NULL DEFAULT 0,
   `reforder` INT NULL DEFAULT 0,
   `isDelete` VARCHAR(1) NULL DEFAULT 'N',
   PRIMARY KEY (`no`),
   INDEX `board_writer_fk_idx` (`writer` ASC) VISIBLE,
   CONSTRAINT `board_writer_fk`
     FOREIGN KEY (`writer`)
     REFERENCES `ksy`.`member` (`userId`)
     ON DELETE SET NULL
     ON UPDATE NO ACTION);
 

-- uploadedfile테이블 수정
ALTER TABLE `ksy`.`uploadedfile` 
ADD COLUMN `boardNo` INT NULL AFTER `fileSize`,
ADD COLUMN `base64String` LONGTEXT NULL AFTER `boardNo`;

-- uploadedfile의 boardNo 외래키 설정
ALTER TABLE `ksy`.`uploadedfile` 
ADD INDEX `uploadedfile_boardNo_fk_idx` (`boardNo` ASC) VISIBLE;

ALTER TABLE `ksy`.`uploadedfile` 
ADD CONSTRAINT `uploadedfile_boardNo_fk`
  FOREIGN KEY (`boardNo`)
  REFERENCES `ksy`.`board` (`no`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;




-- 게시판 전체 글 목록 
select * from board order by no desc;

delete from board where no = 1;

-- 게시판 글 저장 
-- 다음 ref값 가져오는 쿼리문
select max(no) + 1 as nextref from board;

-- 게시글 insert 쿼리문
insert into board(writer, title, content, ref) 
value('blue', '앗싸2빠', '2등이네', (select max(b.no) + 1 from board b));

insert into board(writer, title, content, ref) 
value(?, ?, ?, (select max(b.no) + 1 from board b));

select max(no) + 1 as nextref from board;

-- uploadedfile테이블에 insert (게시판에 업로드한 파일정보)
insert into uploadedfile(originalFileName, ext, newFileName, fileSize, boardNo) values(?,?,?,?,?);

-- ---------게시물 상세 조회
-- 조회수 처리
-- readcoundprocess 테이블 생성
CREATE TABLE `ksy`.`readcountprocess` (
  `no` INT NOT NULL AUTO_INCREMENT,
  `ipAddr` VARCHAR(50) NULL,
  `boardNo` INT NULL,
  `readTime` DATETIME NULL DEFAULT now(),
  PRIMARY KEY (`no`),
  INDEX `rcp_boardNo_fk_idx` (`boardNo` ASC) VISIBLE,
  CONSTRAINT `rcp_boardNo_fk`
    FOREIGN KEY (`boardNo`)
    REFERENCES `ksy`.`board` (`no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
    
-- readcountprocess테이블에 ip주소와 글번호no가 있는지  없는지
	select * from readcountprocess where boardNo = ? and ipAddr = ? ;
    
    
-- ?ipAddr가 ?번 글을 읽은지 24시간이 지났는지 아닌지 (시간차이)
-- timestampdiff()

select timestampdiff(hour, 
(select readTime from readcountprocess where ipAddr = ? and boardNo = ?), now()) as hourDiff;

-- 아이피 주소와 글번호와 읽은 시간을 readcountprocess 테이블에 insert
insert into readcountprocess (ipAddr, boardNo)  
values (?, ?);


-- 아이피 주소와 글번호와 읽은 시간을 readcountprocess 테이블에 update
update readcountprocess set readTime = now()
where ipAddr = ? and boardNo = ?;

-- no번 글의 조회수 증가하는 쿼리문 
update board set readcount = readcount + 1 where no = ?;

-- no번 글 데이터 가져오기
select * from board where no = 1;

-- no번 글의 첨부파일 가져오기
select * from uploadedfile where boardNo = ?;

-- no번 글 삭제
update board set isDelete = 'Y' where no = ?;

-- ---------------- 답글 처리 -------------------
-- 게시글 리스트 출력할 때 정렬 기준
select * from board order by ref desc, reforder asc;

-- 답글을 board테이블에 등록
-- pRef == ref 이고 pRefOrder < reforder인 행에 대해서, reforder = reforder + 1로 업데이트
update board set reforder = reforder + 1
where ref = ? and reforder > ? ;


-- 새로 등록되는 답글 insert:  ref = pRef , step = pStep + 1, reforder = pReforder + 1
insert into board(writer, title, content, ref,  step, reforder) 
values( ?, ?, ?, ?, ?, ?) ;


-- -------------------- 페이징 처리 ---------------------
select * from board order by readcount desc limit 5;
select count(*) as totalPostCnt from board;

-- 한 페이지당 보여줄 글의 갯수 = 3
-- 글의 총 갯수
-- ==> 총 페이지 수 = 글의 총 갯수 / 한페이지당 보여줄 글의 갯수  => 나누어 떨어지지 않으면 + 1

-- 보여주기 시작할 row index번호
-- limit [보여주기 시작할 row index 번호] , 보여줄 row의 갯수

-- 1 페이지
select * from board order by ref desc, reforder asc limit 0, 3;

-- 2 페이지 
select * from board order by ref desc, reforder asc limit 3, 3;

-- 3 페이지
select * from board order by ref desc, reforder asc limit 6, 3;

-- 4 페이지
select * from board order by ref desc, reforder asc limit 9, 3;

-- 5 페이지
select * from board order by ref desc, reforder asc limit 12, 3;

-- limit [보여주기 시작할 row index 번호] 
-- ===> (현재 페이지번호 - 1) * 페이지당 보여줄 글의 갯수


select * from board order by ref desc, reforder asc limit ?, ?;

-- 페이징 블럭 처리
-- 1  2  /  3  4  /  5  6  / 7  (8)
-- 1  2  3   /   4  5  6   /   7  8  (9)

-- 1) 1개의 블럭에 몇개 페이지를 보여줄 것인지 (pageCntPerBlock) : 2
-- ==> 전체 페이징 블럭 갯수 = 전체 페이지 수 / pageCntPerBlock --> 나누어 떨어지지 않으면 + 1

-- 2) 현재 페이지가 속한 페이징 블럭 번호 => 현재페이지 번호 / pageCntPerBlock -> 나누어 떨어지지 않으면 올림
-- 현재 페이지가 2 -> 1번 블럭  : 2 / 2 = 1
-- 현재 페이지가 1 -> 1번 블럭  : 1 / 2 = 0.5 (올림)-> 1
-- 현재 페이지가 5 -> 3번 블럭  : 5 / 2 = 2.5 (올림)-> 3

-- 3) 현재 페이징 블럭 시작 페이지 번호 = (블럭 번호 - 1 ) * pageCntPerBlock + 1


-- 4) 현재 페이징 블럭 끝 페이지 번호 = 블럭 번호 * pageCntPerBlock

-- 검색어(searchWord) + 유형(writer, title, content) 처리
-- 
select count(*) as totalPostCnt from board; -- 전체 게시글 수
-- 검색유형= writer
	select count(*) as totalPostCnt from board 
    where writer like '%whi%' ; 
 
-- 검색유형= title
	select count(*) as totalPostCnt from board 
    where title like ? ; 
    
-- 검색유형= content
select count(*) as totalPostCnt from board 
    where content like ? ; 

-- 검색된 글 목록 
select * from board where writer like '%whi%' order by ref desc, reforder asc limit 0, 3;
select * from board where writer like ? order by ref desc, reforder asc limit ?, ?;







  