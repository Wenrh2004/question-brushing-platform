package com.questionbrushingplatform.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.questionbrushingplatform.common.constant.MessageConstant;

import com.questionbrushingplatform.common.exception.BaseException;
import com.questionbrushingplatform.mapper.QuestionBankMapper;
import com.questionbrushingplatform.mapper.QuestionMapper;
import com.questionbrushingplatform.pojo.dto.PageDTO;
import com.questionbrushingplatform.pojo.dto.QuestionBankAddDTO;
import com.questionbrushingplatform.pojo.entity.Question;
import com.questionbrushingplatform.pojo.entity.QuestionBank;
import com.questionbrushingplatform.pojo.entity.User;
import com.questionbrushingplatform.pojo.query.QuestionBankQuery;
import com.questionbrushingplatform.pojo.vo.QuestionBankVO;
import com.questionbrushingplatform.pojo.vo.UserVO;
import com.questionbrushingplatform.service.QuestionBankService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

    @Autowired
    private QuestionBankMapper questionBankMapper;
    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 通用更新时间
     * @param id
     */
    public void updateTimeById(Long id) {
        QuestionBank questionBank = questionBankMapper.selectById(id);
        questionBank.setUpdateTime(LocalDateTime.now());
        questionBankMapper.updateById(questionBank);
    }


    /**
     * 新增题库
     * @param questionBankAddDTO
     */
    public void add(QuestionBankAddDTO questionBankAddDTO) {
        //必须要有题库名
        if (questionBankAddDTO.getTitle() == null|| questionBankAddDTO.getTitle().isEmpty()) {
            throw new BaseException(MessageConstant.QUESTION_BANK_TITLE_NOT_NULL);
        }
        //先判断题库名是否重复
        LambdaQueryWrapper<QuestionBank> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionBank::getTitle, questionBankAddDTO.getTitle());
        QuestionBank dbQuestionBank = questionBankMapper.selectOne(queryWrapper);
        //如果重复，抛出异常
        if (dbQuestionBank != null) {
            throw new BaseException(MessageConstant.QUESTION_BANK_EXISTS);
        }
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankAddDTO, questionBank);
        questionBank.setUserId(StpUtil.getLoginIdAsLong());
        questionBankMapper.insert(questionBank);

    }

    /**
     * 根据id删除题库
     * @param id
     */
    public void deleteById(Long id) {
        //判断是否是正常的id并且不能重复删除
        if (questionBankMapper.selectById(id) == null) {
            throw new BaseException(MessageConstant.QUEST_BANK_NOT_FOUND);
        }
        //判断该题库下有没有题目，如果有的话，不能删除该题库，要先删除其中的题目
        //查询该题库下是否有题目 select * from question where questionBankId = id
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getQuestionBankId, id);
        Long count = questionMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BaseException(MessageConstant.QUESTION_BANK_NOT_EMPTY);
        }
        //更新时间
        updateTimeById(id);
        questionBankMapper.deleteById(id);


    }

    /**
     * 根据id批量删除题库
     * @param ids
     */
    public void deleteByIds(Long[] ids) {
        LambdaQueryWrapper<QuestionBank> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(QuestionBank::getId, ids);
        //判断是否是正常的id并且不能重复删除
        Long count = questionBankMapper.selectCount(queryWrapper);
        if (count != ids.length){
            throw new BaseException(MessageConstant.QUEST_BANK_NOT_FOUND);
        }
        //判断该题库下有没有题目，如果有的话，不能删除该题库，要先删除其中的题目
        LambdaQueryWrapper<Question> queryWrapperQuestion = new LambdaQueryWrapper<>();
        queryWrapperQuestion.in(Question::getQuestionBankId, ids);
        Long countQuestion = questionMapper.selectCount(queryWrapperQuestion);
        if (countQuestion > 0){
            throw new BaseException(MessageConstant.QUESTION_BANK_NOT_EMPTY);
        }
        //更新时间
        LambdaUpdateWrapper<QuestionBank> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(QuestionBank::getUpdateTime, LocalDateTime.now())
                .in(QuestionBank::getId, ids);
        questionBankMapper.update(null, updateWrapper);
        //删除所选题库
        questionBankMapper.delete(queryWrapper);

    }

    /**
     * 分页查询题库
     * @param questionBankQuery
     * @return
     */
    public PageDTO<QuestionBankVO> selectByPage(QuestionBankQuery questionBankQuery) {
        // 1.构建基础查询条件
        Page<QuestionBank> page = questionBankQuery.toMpPage("createTime", false);
        // 2.分页查询
        Page<QuestionBank> p = lambdaQuery()
                .like(questionBankQuery.getTitle()!=null,QuestionBank::getTitle,questionBankQuery.getTitle())
                .eq(questionBankQuery.getUserId()!=null,QuestionBank::getUserId,questionBankQuery.getUserId())
                .between(questionBankQuery.getStartTime()!=null&&questionBankQuery.getEndTime()!=null,QuestionBank::getCreateTime,questionBankQuery.getStartTime(),questionBankQuery.getEndTime())
                .between(questionBankQuery.getStartTime()!=null&&questionBankQuery.getEndTime()!=null,QuestionBank::getUpdateTime,questionBankQuery.getStartTime(),questionBankQuery.getEndTime())
                .between(questionBankQuery.getStartTime()!=null&&questionBankQuery.getEndTime()!=null,QuestionBank::getEditTime,questionBankQuery.getStartTime(),questionBankQuery.getEndTime())
                .page(page);
        // 3.封装VO结果
        return PageDTO.of(p, QuestionBankVO.class);
    }

}
