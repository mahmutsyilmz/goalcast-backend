package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.dto.league.LeagueCreateRequestDto;
import com.yilmaz.goalCast.dto.league.LeagueDto;
import com.yilmaz.goalCast.dto.league.LeagueUpdateRequestDto;
import com.yilmaz.goalCast.exception.BadRequestException;
import com.yilmaz.goalCast.exception.ResourceNotFoundException;
import com.yilmaz.goalCast.mapper.LeagueMapper;
import com.yilmaz.goalCast.model.League;
import com.yilmaz.goalCast.model.LeagueType;
import com.yilmaz.goalCast.repository.LeagueRepository;
import com.yilmaz.goalCast.service.LeagueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeagueServiceImpl implements LeagueService {

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
    @Transactional
    public LeagueDto createLeague(LeagueCreateRequestDto dto) {
        if ((dto.getLeagueType() == LeagueType.NATIONAL_LEAGUE || dto.getLeagueType() == LeagueType.DOMESTIC_CUP) &&
                dto.getCountry() == null) {
            throw new BadRequestException("Country is required for National League or Domestic Cup types.");
        }

        // Uluslararası turnuvalar için country null olmalı veya dikkate alınmamalı
        if (dto.getLeagueType() == LeagueType.INTERNATIONAL_CLUB ||
                dto.getLeagueType() == LeagueType.INTERNATIONAL_NATIONAL ||
                dto.getLeagueType() == LeagueType.FRIENDLY) { // Hazırlık maçları için de ülke gereksiz olabilir
            dto.setCountry(null); // Frontend null gönderse bile, burada da null olduğundan emin olalım
        }

        League league = leagueMapper.toEntity(dto);
        League savedLeague = leagueRepository.save(league);
        return leagueMapper.toDto(savedLeague);
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

    @Override
    public LeagueDto getLeagueById(Long id) {
        League league = leagueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + id));
        return leagueMapper.toDto(league);
    }
}
