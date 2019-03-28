-- 테이블 강제 삭제 설정
SET foreign_key_checks = 0;

-- 배치 테이블 삭제
drop table batch_job_execution;

drop table batch_job_execution_context;

drop table batch_job_execution_params;

drop table batch_job_execution_seq;

drop table batch_job_instance;

drop table batch_job_seq;

drop table batch_step_execution;

drop table batch_step_execution_context;

drop table batch_step_execution_seq;

-- 로그인 로그 일별 집계 테이블 삭제 
truncate user_login_log_daily;