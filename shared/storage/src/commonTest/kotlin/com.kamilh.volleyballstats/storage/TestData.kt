package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.models.PlayAction.*
import kotlin.time.Duration

fun getMatchReport(id: MatchId): MatchReport =
    matchReportOf(
        matchId = id,
        home = matchTeamOf(
            teamId = teamIdOf(value = 30288),
            code = "ZAW",
            players = listOf(
                playerIdOf(value = 2100771),
                playerIdOf(value = 442),
                playerIdOf(value = 22804),
                playerIdOf(value = 430),
                playerIdOf(value = 118),
                playerIdOf(value = 573),
                playerIdOf(value = 277),
                playerIdOf(value = 2100772),
                playerIdOf(value = 2100773),
                playerIdOf(value = 22790),
                playerIdOf(value = 709),
                playerIdOf(value = 25755),
                playerIdOf(value = 36),
                playerIdOf(value = 563),
            )
        ),
        away = matchTeamOf(
            teamId = teamIdOf(value = 1405),
            code = "JAS",
            players = listOf(
                playerIdOf(value = 589),
                playerIdOf(value = 2100761),
                playerIdOf(value = 63),
                playerIdOf(value = 73),
                playerIdOf(value = 26823),
                playerIdOf(value = 2100762),
                playerIdOf(value = 105),
                playerIdOf(value = 30350),
                playerIdOf(value = 252),
                playerIdOf(value = 22806),
                playerIdOf(value = 28405),
                playerIdOf(value = 633),
                playerIdOf(value = 28394),
                playerIdOf(value = 2100506),
            )
        ),
        mvp = playerIdOf(value = 22806),
        bestPlayer = playerIdOf(value = 430),
        updatedAt = LocalDateTime.parse("2022-04-29T15:20:27.373478"),
        phase = Phase.RegularSeason,
        sets = listOf(
            matchSetOf(
                number = 1,
                score = scoreOf(home = 21, away = 25),
                startTime = ZonedDateTime.parse("2020-09-13T18:36+02:00[Europe/Warsaw]"),
                endTime = ZonedDateTime.parse("2020-09-13T19:01+02:00[Europe/Warsaw]"),
                duration = Duration.parse("25m"),
                points = listOf(
                    MatchPoint(
                        score = scoreOf(home = 1, away = 0),
                        startTime = ZonedDateTime.parse("2020-09-13T18:36:00.565+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:36:15.756+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 430),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = Effect.Invasion,
                                receiverId = playerIdOf(value = 442),
                                setEffect = Effect.Invasion,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = false,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 22806),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Negative, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 430),
                                setterId = playerIdOf(value = 2100773),
                                afterSideOut = true
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ), attackerId = null, rebounderId = playerIdOf(value = 22806), afterSideOut = false
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 442),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Invasion, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 73),
                                attackEffect = Effect.Positive,
                                setEffect = Effect.Invasion
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 73),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Invasion, breakPoint = true
                                ), receiverId = playerIdOf(value = 442), receiveEffect = Effect.Invasion
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Invasion, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 430),
                                attackerPosition = PlayerPosition.P2,
                                attackEffect = Effect.Positive,
                                sideOut = true
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 563),
                                attackerPosition = PlayerPosition.P3,
                                attackEffect = Effect.Perfect,
                                sideOut = false
                            )
                        ),
                        point = TeamId(value = 30288),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 442),
                            p2 = playerIdOf(value = 430),
                            p3 = playerIdOf(value = 563),
                            p4 = playerIdOf(value = 36),
                            p5 = playerIdOf(value = 2100773),
                            p6 = playerIdOf(value = 709)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 73),
                            p2 = playerIdOf(value = 26823),
                            p3 = playerIdOf(value = 22806),
                            p4 = playerIdOf(value = 105),
                            p5 = playerIdOf(value = 2100762),
                            p6 = playerIdOf(value = 633)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 1, away = 1),
                        startTime = ZonedDateTime.parse("2020-09-13T18:36:32.266+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:36:46.160+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 105),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = true,
                                receiveEffect = Effect.Positive,
                                receiverId = playerIdOf(value = 633),
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 26823)
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 442),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Error, breakPoint = true
                                ), attackerId = playerIdOf(value = 105), rebounderId = null, afterSideOut = true
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 633),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 430),
                                attackEffect = Effect.Perfect,
                                setEffect = Effect.Positive
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 430),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = true
                                ), receiverId = playerIdOf(value = 633), receiveEffect = Effect.Positive
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 105),
                                attackerPosition = PlayerPosition.P4,
                                attackEffect = Effect.Perfect,
                                sideOut = true
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 430),
                            p2 = playerIdOf(value = 563),
                            p3 = playerIdOf(value = 36),
                            p4 = playerIdOf(value = 2100773),
                            p5 = playerIdOf(value = 709),
                            p6 = playerIdOf(value = 442)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 589),
                            p2 = playerIdOf(value = 26823),
                            p3 = playerIdOf(value = 22806),
                            p4 = playerIdOf(value = 105),
                            p5 = playerIdOf(value = 2100762),
                            p6 = playerIdOf(value = 633)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 2, away = 1),
                        startTime = ZonedDateTime.parse("2020-09-13T18:37:00.279+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:37:09.287+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 105),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Error, breakPoint = true
                                ),
                                sideOut = false,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = null,
                                setterId = null
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Half, breakPoint = false
                                ), serverId = playerIdOf(value = 26823), attackEffect = null, setEffect = null
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Half, breakPoint = true
                                ), receiverId = playerIdOf(value = 563), receiveEffect = Effect.Half
                            )
                        ),
                        point = TeamId(value = 30288),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 430),
                            p2 = playerIdOf(value = 563),
                            p3 = playerIdOf(value = 36),
                            p4 = playerIdOf(value = 2100773),
                            p5 = playerIdOf(value = 709),
                            p6 = playerIdOf(value = 442)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 26823),
                            p2 = playerIdOf(value = 22806),
                            p3 = playerIdOf(value = 105),
                            p4 = playerIdOf(value = 2100762),
                            p5 = playerIdOf(value = 633),
                            p6 = playerIdOf(value = 589)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 2, away = 2),
                        startTime = ZonedDateTime.parse("2020-09-13T18:37:26.219+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:37:52.537+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 105),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = Effect.Positive,
                                receiverId = playerIdOf(value = 589),
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 26823)
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 709),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = true
                                ),
                                sideOut = false,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 36)
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 709),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = true
                                ),
                                sideOut = false,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = null,
                                setterId = null
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100762),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = false,
                                blockAttempt = false,
                                digAttempt = true,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 26823)
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 36),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 105),
                                setterId = playerIdOf(value = 26823),
                                afterSideOut = true
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 105),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 709),
                                setterId = playerIdOf(value = 36),
                                afterSideOut = false
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Negative, breakPoint = false
                                ), attackerId = null, rebounderId = playerIdOf(value = 36), afterSideOut = false
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 589),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Negative, breakPoint = false
                                ), attackerId = playerIdOf(value = 709), rebounderId = null, afterSideOut = false
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 430),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Error, breakPoint = true
                                ), attackerId = playerIdOf(value = 2100762), rebounderId = null, afterSideOut = false
                            ),
                            Freeball(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = true
                                ), afterSideOut = false
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 589),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 563),
                                attackEffect = Effect.Positive,
                                setEffect = Effect.Positive
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = true
                                ), receiverId = playerIdOf(value = 589), receiveEffect = Effect.Positive
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 105),
                                attackerPosition = PlayerPosition.P3,
                                attackEffect = Effect.Positive,
                                sideOut = true
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100762),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Half, breakPoint = false
                                ), attackerId = null, attackerPosition = null, attackEffect = null, sideOut = false
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 36),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 709),
                                attackerPosition = PlayerPosition.P4,
                                attackEffect = Effect.Negative,
                                sideOut = false
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 2100762),
                                attackerPosition = PlayerPosition.P4,
                                attackEffect = Effect.Perfect,
                                sideOut = false
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 563),
                            p2 = playerIdOf(value = 36),
                            p3 = playerIdOf(value = 2100773),
                            p4 = playerIdOf(value = 709),
                            p5 = playerIdOf(value = 442),
                            p6 = playerIdOf(value = 430)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 26823),
                            p2 = playerIdOf(value = 22806),
                            p3 = playerIdOf(value = 105),
                            p4 = playerIdOf(value = 2100762),
                            p5 = playerIdOf(value = 633),
                            p6 = playerIdOf(value = 589)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 3, away = 2),
                        startTime = ZonedDateTime.parse("2020-09-13T18:38:07.061+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:38:25.914+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 430),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = Effect.Positive,
                                receiverId = playerIdOf(value = 442),
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 430),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = false,
                                blockAttempt = false,
                                digAttempt = true,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Half, breakPoint = true
                                ), attackerId = playerIdOf(value = 430), rebounderId = null, afterSideOut = true
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Error, breakPoint = true
                                ), attackerId = playerIdOf(value = 430), rebounderId = null, afterSideOut = false
                            ),
                            Freeball(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 36),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ), afterSideOut = false
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 442),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 22806),
                                attackEffect = Effect.Positive,
                                setEffect = Effect.Positive
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 22806),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Negative, breakPoint = true
                                ), receiverId = playerIdOf(value = 442), receiveEffect = Effect.Positive
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 430),
                                attackerPosition = PlayerPosition.P6,
                                attackEffect = Effect.Positive,
                                sideOut = true
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 430),
                                attackerPosition = PlayerPosition.P6,
                                attackEffect = Effect.Perfect,
                                sideOut = false
                            )
                        ),
                        point = TeamId(value = 30288),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 563),
                            p2 = playerIdOf(value = 36),
                            p3 = playerIdOf(value = 2100773),
                            p4 = playerIdOf(value = 709),
                            p5 = playerIdOf(value = 442),
                            p6 = playerIdOf(value = 430)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 22806),
                            p2 = playerIdOf(value = 105),
                            p3 = playerIdOf(value = 2100762),
                            p4 = playerIdOf(value = 633),
                            p5 = playerIdOf(value = 589),
                            p6 = playerIdOf(value = 26823)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 3, away = 3),
                        startTime = ZonedDateTime.parse("2020-09-13T18:38:40.491+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:38:52.714+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100762),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = true,
                                digAttempt = false,
                                receiveEffect = Effect.Perfect,
                                receiverId = playerIdOf(value = 633),
                                setEffect = Effect.Perfect,
                                setterId = playerIdOf(value = 26823)
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 709),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Error, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 2100762),
                                setterId = playerIdOf(value = 26823),
                                afterSideOut = true
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 633),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 36),
                                attackEffect = Effect.Perfect,
                                setEffect = Effect.Perfect
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 36),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = true
                                ), receiverId = playerIdOf(value = 633), receiveEffect = Effect.Perfect
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 2100762),
                                attackerPosition = PlayerPosition.P3,
                                attackEffect = Effect.Perfect,
                                sideOut = true
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 36),
                            p2 = playerIdOf(value = 2100773),
                            p3 = playerIdOf(value = 709),
                            p4 = playerIdOf(value = 2100772),
                            p5 = playerIdOf(value = 430),
                            p6 = playerIdOf(value = 563)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 22806),
                            p2 = playerIdOf(value = 105),
                            p3 = playerIdOf(value = 2100762),
                            p4 = playerIdOf(value = 633),
                            p5 = playerIdOf(value = 589),
                            p6 = playerIdOf(value = 26823)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 3, away = 4),
                        startTime = ZonedDateTime.parse("2020-09-13T18:39:25.868+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:39:34.613+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Error, breakPoint = false
                                ), serverId = playerIdOf(value = 105), attackEffect = null, setEffect = null
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 105),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = true
                                ), receiverId = playerIdOf(value = 563), receiveEffect = Effect.Error
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 442),
                            p2 = playerIdOf(value = 2100773),
                            p3 = playerIdOf(value = 709),
                            p4 = playerIdOf(value = 2100772),
                            p5 = playerIdOf(value = 430),
                            p6 = playerIdOf(value = 563)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 105),
                            p2 = playerIdOf(value = 2100762),
                            p3 = playerIdOf(value = 633),
                            p4 = playerIdOf(value = 73),
                            p5 = playerIdOf(value = 26823),
                            p6 = playerIdOf(value = 22806)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 3, away = 5),
                        startTime = ZonedDateTime.parse("2020-09-13T18:39:51.406+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:40:09.039+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 430),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = Effect.Positive,
                                receiverId = playerIdOf(value = 442),
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 709),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Half, breakPoint = false
                                ),
                                sideOut = false,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 563)
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100762),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 709),
                                setterId = playerIdOf(value = 563),
                                afterSideOut = false
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 105),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Half, breakPoint = true
                                ), attackerId = playerIdOf(value = 430), rebounderId = null, afterSideOut = true
                            ),
                            Freeball(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100772),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = false
                                ), afterSideOut = false
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 442),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 105),
                                attackEffect = Effect.Positive,
                                setEffect = Effect.Positive
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 105),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Negative, breakPoint = true
                                ), receiverId = playerIdOf(value = 442), receiveEffect = Effect.Positive
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 430),
                                attackerPosition = PlayerPosition.P5,
                                attackEffect = Effect.Positive,
                                sideOut = true
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 709),
                                attackerPosition = PlayerPosition.P3,
                                attackEffect = Effect.Half,
                                sideOut = false
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 442),
                            p2 = playerIdOf(value = 2100773),
                            p3 = playerIdOf(value = 709),
                            p4 = playerIdOf(value = 2100772),
                            p5 = playerIdOf(value = 430),
                            p6 = playerIdOf(value = 563)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 105),
                            p2 = playerIdOf(value = 2100762),
                            p3 = playerIdOf(value = 633),
                            p4 = playerIdOf(value = 73),
                            p5 = playerIdOf(value = 26823),
                            p6 = playerIdOf(value = 22806)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 3, away = 6),
                        startTime = ZonedDateTime.parse("2020-09-13T18:40:34.473+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:40:46.295+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 709),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Error, breakPoint = false
                                ), serverId = playerIdOf(value = 105), attackEffect = null, setEffect = null
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 105),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = true
                                ), receiverId = playerIdOf(value = 709), receiveEffect = Effect.Error
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 442),
                            p2 = playerIdOf(value = 2100773),
                            p3 = playerIdOf(value = 709),
                            p4 = playerIdOf(value = 2100772),
                            p5 = playerIdOf(value = 430),
                            p6 = playerIdOf(value = 563)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 105),
                            p2 = playerIdOf(value = 2100762),
                            p3 = playerIdOf(value = 633),
                            p4 = playerIdOf(value = 73),
                            p5 = playerIdOf(value = 26823),
                            p6 = playerIdOf(value = 22806)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 4, away = 6),
                        startTime = ZonedDateTime.parse("2020-09-13T18:41:45.724+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:41:56.977+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 105),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Error, breakPoint = true
                                ), receiverId = null, receiveEffect = null
                            )
                        ),
                        point = TeamId(value = 30288),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 442),
                            p2 = playerIdOf(value = 2100773),
                            p3 = playerIdOf(value = 709),
                            p4 = playerIdOf(value = 2100772),
                            p5 = playerIdOf(value = 430),
                            p6 = playerIdOf(value = 563)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 105),
                            p2 = playerIdOf(value = 2100762),
                            p3 = playerIdOf(value = 633),
                            p4 = playerIdOf(value = 73),
                            p5 = playerIdOf(value = 26823),
                            p6 = playerIdOf(value = 22806)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 4, away = 7),
                        startTime = ZonedDateTime.parse("2020-09-13T18:42:11.905+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:42:20.876+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 22806),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = true,
                                receiveEffect = Effect.Perfect,
                                receiverId = playerIdOf(value = 633),
                                setEffect = Effect.Perfect,
                                setterId = playerIdOf(value = 26823)
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Error, breakPoint = true
                                ), attackerId = playerIdOf(value = 22806), rebounderId = null, afterSideOut = true
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 633),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 2100773),
                                attackEffect = Effect.Perfect,
                                setEffect = Effect.Perfect
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = true
                                ), receiverId = playerIdOf(value = 633), receiveEffect = Effect.Perfect
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 22806),
                                attackerPosition = PlayerPosition.P6,
                                attackEffect = Effect.Perfect,
                                sideOut = true
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 2100773),
                            p2 = playerIdOf(value = 709),
                            p3 = playerIdOf(value = 2100772),
                            p4 = playerIdOf(value = 430),
                            p5 = playerIdOf(value = 563),
                            p6 = playerIdOf(value = 442)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 589),
                            p2 = playerIdOf(value = 2100762),
                            p3 = playerIdOf(value = 633),
                            p4 = playerIdOf(value = 73),
                            p5 = playerIdOf(value = 26823),
                            p6 = playerIdOf(value = 22806)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 4, away = 8),
                        startTime = ZonedDateTime.parse("2020-09-13T18:42:41.665+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:42:53.554+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100772),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Half, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = Effect.Positive,
                                receiverId = playerIdOf(value = 563),
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 73),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 2100772),
                                setterId = playerIdOf(value = 2100773),
                                afterSideOut = true
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 2100762),
                                attackEffect = Effect.Half,
                                setEffect = Effect.Positive
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100762),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Negative, breakPoint = true
                                ), receiverId = playerIdOf(value = 563), receiveEffect = Effect.Positive
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 2100772),
                                attackerPosition = PlayerPosition.P3,
                                attackEffect = Effect.Half,
                                sideOut = true
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 2100773),
                            p2 = playerIdOf(value = 709),
                            p3 = playerIdOf(value = 2100772),
                            p4 = playerIdOf(value = 430),
                            p5 = playerIdOf(value = 563),
                            p6 = playerIdOf(value = 442)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 2100762),
                            p2 = playerIdOf(value = 633),
                            p3 = playerIdOf(value = 73),
                            p4 = playerIdOf(value = 26823),
                            p5 = playerIdOf(value = 22806),
                            p6 = playerIdOf(value = 589)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 5, away = 8),
                        startTime = ZonedDateTime.parse("2020-09-13T18:43:06.779+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:43:15.073+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100762),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Error, breakPoint = true
                                ), receiverId = null, receiveEffect = null
                            )
                        ),
                        point = TeamId(value = 30288),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 2100773),
                            p2 = playerIdOf(value = 709),
                            p3 = playerIdOf(value = 2100772),
                            p4 = playerIdOf(value = 430),
                            p5 = playerIdOf(value = 563),
                            p6 = playerIdOf(value = 442)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 2100762),
                            p2 = playerIdOf(value = 633),
                            p3 = playerIdOf(value = 73),
                            p4 = playerIdOf(value = 26823),
                            p5 = playerIdOf(value = 22806),
                            p6 = playerIdOf(value = 589)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 5, away = 9),
                        startTime = ZonedDateTime.parse("2020-09-13T18:43:29.861+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:43:39.646+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 709),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Error, breakPoint = true
                                ), receiverId = null, receiveEffect = null
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 709),
                            p2 = playerIdOf(value = 2100772),
                            p3 = playerIdOf(value = 430),
                            p4 = playerIdOf(value = 563),
                            p5 = playerIdOf(value = 442),
                            p6 = playerIdOf(value = 2100773)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 2100762),
                            p2 = playerIdOf(value = 633),
                            p3 = playerIdOf(value = 73),
                            p4 = playerIdOf(value = 26823),
                            p5 = playerIdOf(value = 22806),
                            p6 = playerIdOf(value = 589)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 5, away = 10),
                        startTime = ZonedDateTime.parse("2020-09-13T18:43:52.691+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:44:06.315+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 430),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Half, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = Effect.Invasion,
                                receiverId = playerIdOf(value = 442),
                                setEffect = Effect.Invasion,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 22806),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 430),
                                setterId = playerIdOf(value = 2100773),
                                afterSideOut = true
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 442),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Invasion, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 633),
                                attackEffect = Effect.Half,
                                setEffect = Effect.Invasion
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 633),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Invasion, breakPoint = true
                                ), receiverId = playerIdOf(value = 442), receiveEffect = Effect.Invasion
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Invasion, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 430),
                                attackerPosition = PlayerPosition.P3,
                                attackEffect = Effect.Half,
                                sideOut = true
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 709),
                            p2 = playerIdOf(value = 2100772),
                            p3 = playerIdOf(value = 430),
                            p4 = playerIdOf(value = 563),
                            p5 = playerIdOf(value = 442),
                            p6 = playerIdOf(value = 2100773)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 633),
                            p2 = playerIdOf(value = 73),
                            p3 = playerIdOf(value = 26823),
                            p4 = playerIdOf(value = 22806),
                            p5 = playerIdOf(value = 589),
                            p6 = playerIdOf(value = 2100762)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 6, away = 10),
                        startTime = ZonedDateTime.parse("2020-09-13T18:44:20.700+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:44:39.588+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100772),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = Effect.Invasion,
                                receiverId = playerIdOf(value = 563),
                                setEffect = Effect.Invasion,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = false,
                                blockAttempt = true,
                                digAttempt = false,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 73),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Negative, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 2100772),
                                setterId = playerIdOf(value = 2100773),
                                afterSideOut = true
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Error, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 563),
                                setterId = playerIdOf(value = 2100773),
                                afterSideOut = false
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100762),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Half, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 2100772),
                                rebounderId = playerIdOf(value = 73),
                                afterSideOut = true
                            ),
                            Freeball(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ), afterSideOut = false
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Invasion, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 633),
                                attackEffect = Effect.Positive,
                                setEffect = Effect.Invasion
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 633),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Invasion, breakPoint = true
                                ), receiverId = playerIdOf(value = 563), receiveEffect = Effect.Invasion
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Invasion, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 2100772),
                                attackerPosition = PlayerPosition.P2,
                                attackEffect = Effect.Positive,
                                sideOut = true
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 563),
                                attackerPosition = PlayerPosition.P4,
                                attackEffect = Effect.Perfect,
                                sideOut = false
                            )
                        ),
                        point = TeamId(value = 30288),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 709),
                            p2 = playerIdOf(value = 2100772),
                            p3 = playerIdOf(value = 430),
                            p4 = playerIdOf(value = 563),
                            p5 = playerIdOf(value = 442),
                            p6 = playerIdOf(value = 2100773)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 633),
                            p2 = playerIdOf(value = 73),
                            p3 = playerIdOf(value = 26823),
                            p4 = playerIdOf(value = 22806),
                            p5 = playerIdOf(value = 589),
                            p6 = playerIdOf(value = 2100762)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 6, away = 11),
                        startTime = ZonedDateTime.parse("2020-09-13T18:44:54.748+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:45:05.480+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 73),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = Effect.Perfect,
                                receiverId = playerIdOf(value = 22806),
                                setEffect = Effect.Perfect,
                                setterId = playerIdOf(value = 26823)
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 22806),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 2100772),
                                attackEffect = Effect.Perfect,
                                setEffect = Effect.Perfect
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100772),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = true
                                ), receiverId = playerIdOf(value = 22806), receiveEffect = Effect.Perfect
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 73),
                                attackerPosition = PlayerPosition.P2,
                                attackEffect = Effect.Perfect,
                                sideOut = true
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 2100772),
                            p2 = playerIdOf(value = 430),
                            p3 = playerIdOf(value = 563),
                            p4 = playerIdOf(value = 36),
                            p5 = playerIdOf(value = 2100773),
                            p6 = playerIdOf(value = 709)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 633),
                            p2 = playerIdOf(value = 73),
                            p3 = playerIdOf(value = 26823),
                            p4 = playerIdOf(value = 22806),
                            p5 = playerIdOf(value = 589),
                            p6 = playerIdOf(value = 2100762)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 7, away = 11),
                        startTime = ZonedDateTime.parse("2020-09-13T18:45:17.648+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:45:29.550+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 36),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = true,
                                digAttempt = false,
                                receiveEffect = Effect.Invasion,
                                receiverId = playerIdOf(value = 442),
                                setEffect = Effect.Invasion,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 105),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Error, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 36),
                                setterId = playerIdOf(value = 2100773),
                                afterSideOut = true
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 442),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Invasion, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 73),
                                attackEffect = Effect.Perfect,
                                setEffect = Effect.Invasion
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 73),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Invasion, breakPoint = true
                                ), receiverId = playerIdOf(value = 442), receiveEffect = Effect.Invasion
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Invasion, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 36),
                                attackerPosition = PlayerPosition.P4,
                                attackEffect = Effect.Perfect,
                                sideOut = true
                            )
                        ),
                        point = TeamId(value = 30288),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 442),
                            p2 = playerIdOf(value = 430),
                            p3 = playerIdOf(value = 563),
                            p4 = playerIdOf(value = 36),
                            p5 = playerIdOf(value = 2100773),
                            p6 = playerIdOf(value = 709)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 73),
                            p2 = playerIdOf(value = 26823),
                            p3 = playerIdOf(value = 22806),
                            p4 = playerIdOf(value = 105),
                            p5 = playerIdOf(value = 2100762),
                            p6 = playerIdOf(value = 633)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 7, away = 12),
                        startTime = ZonedDateTime.parse("2020-09-13T18:45:42.583+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:46:23.699+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 22806),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Negative, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = Effect.Invasion,
                                receiverId = playerIdOf(value = 633),
                                setEffect = Effect.Invasion,
                                setterId = playerIdOf(value = 26823)
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 430),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = true
                                ),
                                sideOut = false,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 2100773)
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 633),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                sideOut = false,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 26823)
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100762),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                sideOut = false,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 26823)
                            ),
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 22806),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = false,
                                blockAttempt = true,
                                digAttempt = false,
                                receiveEffect = null,
                                receiverId = null,
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 589)
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 36),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 22806),
                                setterId = playerIdOf(value = 26823),
                                afterSideOut = true
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 2100762),
                                setterId = playerIdOf(value = 26823),
                                afterSideOut = false
                            ),
                            Block(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Error, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 22806),
                                setterId = playerIdOf(value = 589),
                                afterSideOut = false
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 442),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 22806),
                                rebounderId = playerIdOf(value = 36),
                                afterSideOut = true
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 589),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Negative, breakPoint = false
                                ), attackerId = playerIdOf(value = 430), rebounderId = null, afterSideOut = false
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 442),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = true
                                ), attackerId = playerIdOf(value = 633), rebounderId = null, afterSideOut = false
                            ),
                            Dig(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Negative, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 430),
                                rebounderId = playerIdOf(value = 563),
                                afterSideOut = false
                            ),
                            Freeball(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 22806),
                                        position = PlayerPosition.P3,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ), afterSideOut = false
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 633),
                                        position = PlayerPosition.P6,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Invasion, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 430),
                                attackEffect = Effect.Negative,
                                setEffect = Effect.Invasion
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 430),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Invasion, breakPoint = true
                                ), receiverId = playerIdOf(value = 633), receiveEffect = Effect.Invasion
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Invasion, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 22806),
                                attackerPosition = PlayerPosition.P3,
                                attackEffect = Effect.Negative,
                                sideOut = true
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = true
                                ),
                                attackerId = playerIdOf(value = 430),
                                attackerPosition = PlayerPosition.P1,
                                attackEffect = Effect.Positive,
                                sideOut = false
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 633),
                                attackerPosition = PlayerPosition.P6,
                                attackEffect = Effect.Positive,
                                sideOut = false
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 2100773),
                                        position = PlayerPosition.P4,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Half, breakPoint = true
                                ), attackerId = null, attackerPosition = null, attackEffect = null, sideOut = false
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 2100762),
                                attackerPosition = PlayerPosition.P5,
                                attackEffect = Effect.Positive,
                                sideOut = false
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 589),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 22806),
                                attackerPosition = PlayerPosition.P3,
                                attackEffect = Effect.Perfect,
                                sideOut = false
                            )
                        ),
                        point = TeamId(value = 1405),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 430),
                            p2 = playerIdOf(value = 563),
                            p3 = playerIdOf(value = 36),
                            p4 = playerIdOf(value = 2100773),
                            p5 = playerIdOf(value = 709),
                            p6 = playerIdOf(value = 442)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 589),
                            p2 = playerIdOf(value = 26823),
                            p3 = playerIdOf(value = 22806),
                            p4 = playerIdOf(value = 105),
                            p5 = playerIdOf(value = 2100762),
                            p6 = playerIdOf(value = 633)
                        )
                    ),
                    MatchPoint(
                        score = scoreOf(home = 8, away = 12),
                        startTime = ZonedDateTime.parse("2020-09-13T18:46:48.069+02:00[Europe/Warsaw]"),
                        endTime = ZonedDateTime.parse("2020-09-13T18:47:01.550+02:00[Europe/Warsaw]"),
                        playActions = listOf(
                            Attack(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 430),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Perfect, breakPoint = false
                                ),
                                sideOut = true,
                                blockAttempt = false,
                                digAttempt = false,
                                receiveEffect = Effect.Negative,
                                receiverId = playerIdOf(value = 709),
                                setEffect = Effect.Positive,
                                setterId = playerIdOf(value = 563)
                            ),
                            Receive(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 709),
                                        position = PlayerPosition.P5,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Negative, breakPoint = false
                                ),
                                serverId = playerIdOf(value = 26823),
                                attackEffect = Effect.Perfect,
                                setEffect = Effect.Positive
                            ),
                            Serve(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 26823),
                                        position = PlayerPosition.P1,
                                        teamId = TeamId(value = 1405)
                                    ), effect = Effect.Positive, breakPoint = true
                                ), receiverId = playerIdOf(value = 709), receiveEffect = Effect.Negative
                            ),
                            Set(
                                generalInfo = GeneralInfo(
                                    playerInfo = PlayerInfo(
                                        playerId = playerIdOf(value = 563),
                                        position = PlayerPosition.P2,
                                        teamId = TeamId(value = 30288)
                                    ), effect = Effect.Positive, breakPoint = false
                                ),
                                attackerId = playerIdOf(value = 430),
                                attackerPosition = PlayerPosition.P1,
                                attackEffect = Effect.Perfect,
                                sideOut = true
                            )
                        ),
                        point = TeamId(value = 30288),
                        homeLineup = Lineup(
                            p1 = playerIdOf(value = 430),
                            p2 = playerIdOf(value = 563),
                            p3 = playerIdOf(value = 36),
                            p4 = playerIdOf(value = 2100773),
                            p5 = playerIdOf(value = 709),
                            p6 = playerIdOf(value = 442)
                        ),
                        awayLineup = Lineup(
                            p1 = playerIdOf(value = 26823),
                            p2 = playerIdOf(value = 22806),
                            p3 = playerIdOf(value = 105),
                            p4 = playerIdOf(value = 2100762),
                            p5 = playerIdOf(value = 633),
                            p6 = playerIdOf(value = 589)
                        )
                    ),
                )
            )
        ),
    )