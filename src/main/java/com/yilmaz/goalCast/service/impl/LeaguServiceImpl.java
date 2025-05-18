package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.dto.league.LeagueCreateRequestDto;
import com.yilmaz.goalCast.dto.league.LeagueDto;
import com.yilmaz.goalCast.dto.league.LeagueUpdateRequestDto;
import com.yilmaz.goalCast.exception.ResourceNotFoundException;
import com.yilmaz.goalCast.mapper.LeagueMapper;
import com.yilmaz.goalCast.model.League;
import com.yilmaz.goalCast.repository.LeagueRepository;
import com.yilmaz.goalCast.service.LeagueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaguServiceImpl implements LeagueService {

    private final LeagueRepository leagueRepository;
    private final LeagueMapper leagueMapper;
    @Override
    public List<LeagueDto> getAllLeagues() {
        return leagueRepository.findAll()
                .stream()
                .map(leagueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeagueDto createLeague(LeagueCreateRequestDto dto) {
        League league = leagueMapper.toEntity(dto);
        League saved = leagueRepository.save(league);
        return leagueMapper.toDto(saved);
    }

    @Override
    public LeagueDto updateLeague(Long id, LeagueUpdateRequestDto dto) {
        League league = leagueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + id));
        leagueMapper.updateEntityFromDto(dto, league);
        League updated = leagueRepository.save(league);
        return leagueMapper.toDto(updated);
    }

    @Override
    public void deleteLeague(Long id) {
        League league = leagueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + id));
        leagueRepository.delete(league);
    }
}
