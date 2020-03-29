import application.ApplicationStyle
import application.ApplicationView
import application.arcadiaViews.InMatchStyle
import application.arcadiaViews.ScoreStyle
import application.gearnetViews.GearNetStyle
import arcadia.Arcadia
import javafx.stage.Stage
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.launch

fun main(args: Array<String>) {
    launch<MyApp>(args)
}

class MyApp : App(ApplicationView::class, ApplicationStyle::class, ScoreStyle::class, InMatchStyle::class, GearNetStyle::class) {

    private val arcadia: Arcadia by inject()

    companion object {
        const val GEARNET_ENABLED = false
        const val SIMULATION_MODE = false
        const val BORDER_TRACINGS = false
        const val TWITCH_CHAT_BOT = true
        const val VERSION = "0.7.3"
    }

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        when(GEARNET_ENABLED) {
            true -> view.title = "ＧｅａｒＮｅｔ // $VERSION"
            false -> view.title = "Gᴜɪʟᴛy Åʀᴄᴀᴅɪᴀ // $VERSION"
        }
    }

    override fun start(stage: Stage) {
        when(GEARNET_ENABLED) {
            true -> {
                stage.width  = 1600.0 + 16
                stage.height = 900.0 + 39
                stage.isFullScreen = false
            }
            false -> {
                stage.width  = 1904.0 + 16
                stage.height = 1041.0 + 39
                stage.isFullScreen = true
            }
        }
        stage.isResizable = false
        super.start(stage)
        stage.toBack()
        stage.apply { }
        arcadia.startArcadia()
    }

}

/*

NOTE ------------------------------ BOUNTIES ------------------------------


    ⚙️ BOUNTY REWARD:

    The reward in W$ Bounty points that the victor takes from their fallen opponent.

    ❓ For What Purpose: You should be higher on the scoreboard if you win and lower if you lose.


    ⚙️ BOUNTY INFLATE:

    After every fight, increase every player's Bounty by (matchesWon + matchesSum) * (bountyInflate)%.
    Every match, win or loss, will reward a Fighter with a BI bonus. The longer a Fighter has been
    participating in the lobby, the higher their base value is increased before BI bonus is applied.

    ❓ For What Purpose: Since a dominant Fighter will eventually reduce most other Fighters' Bounty
    to almost nothing, their reward comes in the form of scaled up passive bounty gains.
    Since the base BI is based on 1 W$ per match, and 1 additional W$ per win, it rewards
    Fighters who have been participating in the lobby for longer, but not by much.


    ⚙️ SHADOW BONUS: (Total Matches Played Bonus)

    Bounty Bets should be played as a full 80 game set.
    The rules automatically change slightly every 10 games.
    Mostly in a way that escalates rewards near end-game.

    ❓ For What Purpose: As the game progresses, scores inflate, and leaderboards become more defined,
    the Shadow Bonus increases every 10 matches to add a bit more volatility to the late game.

    Match Number  = Title   (Effect)
    ------------    -----   ------------------------------
    1-10          = Initial (+0 global betOnPayout % and bountyInflate %)
    11-20         = 1st 10  (+80 global betOnPayout % and bountyInflate %)
    21-30         = 2nd 10  (+160 global betOnPayout % and bountyInflate %)
    31-40         = 3rd 10  (+240 global betOnPayout % and bountyInflate %)
    41-50         = 4th 10  (+320 global betOnPayout % and bountyInflate %)
    51-60         = 5th 10  (+400 global betOnPayout % and bountyInflate %)
    61-70         = 6th 10  (+560 global betOnPayout % and bountyInflate %)
    71-80         = Last 10 (+800 global betOnPayout % and bountyInflate %)


    ⚙️ CAPTURE & SCOUTING: (Straight Win Bonus & Round 3 Effects)

    By default, after defeating an opponent, 32% of their Bounty is taken.
    A Straight Win Bonus makes top-heavy Bounties fall harder and grant bigger rewards.
    That is, given the player resolves a match within 2 Rounds, as the bonus implies.
    This outcome can be bet on using the Black and White betting chips.

    ❓ For What Purpose: C&S is meant to add a bit more dynamic to how Bounty payouts work.

    Rating Diff  = Title        (Effect)
    -----------    ------       ------------------------------
    +16          = ANTAGONIST   (2R win gives victorBounty +16% of fallenBounty, +0% for 3R Win)
    +15          =              (2R win gives victorBounty +17% of fallenBounty, +2% for 3R Win)
    +14          =              (2R win gives victorBounty +18% of fallenBounty, +4% for 3R Win)
    +13          =              (2R win gives victorBounty +19% of fallenBounty, +6% for 3R Win)
    +12          =              (2R win gives victorBounty +20% of fallenBounty, +8% for 3R Win)
    +11          =              (2R win gives victorBounty +21% of fallenBounty, +10% for 3R Win)
    +10          =              (2R win gives victorBounty +22% of fallenBounty, +12% for 3R Win)
    +9           =              (2R win gives victorBounty +23% of fallenBounty, +14% for 3R Win)
    +8           = BULLY        (2R win gives victorBounty +24% of fallenBounty, +16% for 3R Win)
    +7           =              (2R win gives victorBounty +25% of fallenBounty, +18% for 3R Win)
    +6           =              (2R win gives victorBounty +26% of fallenBounty, +20% for 3R Win)
    +5           =              (2R win gives victorBounty +27% of fallenBounty, +22% for 3R Win)
    +4           =              (2R win gives victorBounty +28% of fallenBounty, +24% for 3R Win)
    +3           =              (2R win gives victorBounty +29% of fallenBounty, +26% for 3R Win)
    +2           =              (2R win gives victorBounty +30% of fallenBounty, +28% for 3R Win)
    +1           =              (2R win gives victorBounty +31% of fallenBounty, +30% for 3R Win)
    0±           = EVEN MATCHUP (gives victorBounty +32% of fallenBounty)
    -1           =              (2R win gives victorBounty +34% of fallenBounty, +33% for 3R Win)
    -2           =              (2R win gives victorBounty +36% of fallenBounty, +34% for 3R Win)
    -3           =              (2R win gives victorBounty +38% of fallenBounty, +35% for 3R Win)
    -4           =              (2R win gives victorBounty +40% of fallenBounty, +36% for 3R Win)
    -5           =              (2R win gives victorBounty +42% of fallenBounty, +37% for 3R Win)
    -6           =              (2R win gives victorBounty +44% of fallenBounty, +38% for 3R Win)
    -7           =              (2R win gives victorBounty +46% of fallenBounty, +39% for 3R Win)
    -8           = UPHILL       (2R win gives victorBounty +48% of fallenBounty, +40% for 3R Win)
    -9           =              (2R win gives victorBounty +50% of fallenBounty, +41% for 3R Win)
    -10          =              (2R win gives victorBounty +52% of fallenBounty, +42% for 3R Win)
    -11          =              (2R win gives victorBounty +54% of fallenBounty, +43% for 3R Win)
    -12          =              (2R win gives victorBounty +56% of fallenBounty, +44% for 3R Win)
    -13          =              (2R win gives victorBounty +58% of fallenBounty, +45% for 3R Win)
    -14          =              (2R win gives victorBounty +60% of fallenBounty, +46% for 3R Win)
    -15          =              (2R win gives victorBounty +62% of fallenBounty, +47% for 3R Win)
    -16          = THE UNDERDOG (2R win gives victorBounty +64% of fallenBounty, +48% for 3R Win)




    NOTE ------------------------------ BETTING ------------------------------


    ⚙️ RISK RATING: (Wicked Hot Metal Text)

    RR represents a Fighter's win streak, kind of. A Fighter loses -2 RR when defeated,
    and gains +1 RR when victorious. So the "streak" doesn't reset, so much as it is
    damaged from a loss. RR grants several small bonuses and detriments as it ranks up.

    ❓ For What Purpose: Risk Rating reward consistent wins with an exponentially increasing
    bonus to bountyInflate. Since a dominant Fighter will eventually reduce most other
    Fighters' Bounty to almost nothing, their reward comes in the form a massively scaled
    up passive bounty gains. It's a completely different story on the betting side of things,
    as a high Risk Rating will reduce payouts on a Fighter, since the odds are much higher.

    Risk Rating  = Title        (Effect)
    -----------   --------      ------------------------------
    ±0 NEUTRAL   =              (0± bountyInflate %, 0± betOnPayout %, 0± betOffPayout %)
    +1           = C            (+40 bountyInflate %, -8 betOnPayout %, +16 betOffPayout %)
    +2           = C+           (+80 bountyInflate %, -16 betOnPayout %, +32 betOffPayout %)
    +3           = B            (+160 bountyInflate %, -24 betOnPayout %, +64 betOffPayout %)
    +4           = B+           (+320 bountyInflate %, -32 betOnPayout %, +128 betOffPayout %)
    +5           = A            (+640 bountyInflate %, -40 betOnPayout %, +256 betOffPayout %)
    +6           = A+           (+1280 bountyInflate %, -48 betOnPayout %, +512 betOffPayout %)
    +7           = S            (+2560 bountyInflate %, -56 betOnPayout %, +1024 betOffPayout %)
    +8 APEX      = BOSS         (+5120 bountyInflate %, -64 betOnPayout %, +2048 betOffPayout %)


    ⚙️ GRUDGE RATING: (Spooky Blue Spirit Orbs)

    Every point of GR adds to a Fighter's finalBonusPayout %.
    GR can -NOT- start gaining until RR is depleted. Every time a Fighter loses to someone,
    they stack GR , maxing at 8. Once a Fighter defeats an opponent, global GR values reset to 0.

    ❓ For What Purpose: GR is meant to raise the stakes when a particularly dominant Fighter joins.
    The upper end of GR's betting betOnPayout is quite high due to the fact that reaching max GR
    will be somewhat rare. Since GR is applied individually and reset globally, it only takes a
    single win to relieve everyone of their GR. The bountyInflate bonus that comes with it is
    meant to reward (albeit small) persistent attempts against a strong opponent, despite the loss.
    GR resets after the payout relevant to the reset.

    Grudge Rating  = Title              (Effect)
    -------------   ----------------   ------------------------------
    ±0 NEUTRAL     =                    (0± bountyInflate %, 0± betOnPayout %, 0± betOffPayout %)
    -1             = Salty              (+4 bountyInflate %, +40 betOnPayout %, -2 betOffPayout %)
    -2             = Barbed             (+8 bountyInflate %, +80 betOnPayout %, -4 betOffPayout %)
    -3             = Spiteful           (+16 bountyInflate %, +160 betOnPayout %, -6 betOffPayout %)
    -4             = Malicious          (+32 bountyInflate %, +320 betOnPayout %, -8 betOffPayout %)
    -5             = Relentless         (+64 bountyInflate %, +640 betOnPayout %, -10 betOffPayout %)
    -6             = Malevolent         (+128 bountyInflate %, +1280 betOnPayout %, -12 betOffPayout %)
    -7             = Bloodthirsty       (+256 bountyInflate %, +2560 betOnPayout %, -14 betOffPayout %)
    -8 NADIR       = VENGEFUL SPIRIT    (+512 bountyInflate %, +5120 betOnPayout %, -16 betOffPayout %)


    ⚙️ BETTING CHIPS:

    • Viewers can bet a maximum of 10 Chips on any given match
    • They do so by pasting 1 Chip emoji per 10% of their wallet they'd like to wager
    • 10 * (wallet * 10%) = 100% (All-In), hence the 10 Chips maximum
    • The 10 Chips can be posted over several chat messages
    • Every Chip played over 10 will FAULT the earliest Chip added, forfeiting its wager
    • Any wallet below 100 W$ after payout will be reset to the 100 W$ wallet minimum
    • Only Gambler scores over 1000 W$ will appear on the Leaderboard

    ❓ For What Purpose:


    MAIN CHIPS: (These are definitely in the game)

    🔴 Red Chip: Wager 10% that Red will win, ±N betOnPayout % (N = TOTAL Red Chips)
    🔵 Blu Chip: Wager 10% that Blu will win, ±N betOnPayout % (N = TOTAL Blu Chips)
    ⚪ White Chip: Wager 10%, and 10 to finalBonusPayout %, Match will be 2 rounds
    ⚫ Black Chip: Wager 10%, and 10 to finalBonusPayout %, Match will be 3 rounds


    BONUS CHIPS: (These might be added later if the capability is there)

    🟣 Stolen Purple Chip: Pays 100% for each, wager 10% that a Burst gets thrown
    🟠 Killer Orange Chip: Pays 80% for each, wager 10% that an IK will occur
    🟡 Superb Yellow Chip: Pays 60% for each, wager 10% that a Perfect will occur
    🟢 Speedy Forest Chip: Pays 40% for each, wager 10% that a Round resolves under 20 seconds
    🟤 Modest Copper Chip: Pays 20% for each, wager 10% that Rakusyo occurs




    NOTE ------------------------------ REPLAY AND UNDO ------------------------------


    ⚙️ EVENT STACK:

    • Every event should be stored in the eventHistory stack, discarded given duplicate timestamps
    • The eventHistory stack should often write to plain-text, sessionID appended to its filename
    • Undo quickly runs the entire simulation back to the current spot, minus 1 event
    • Load a replay on startup to test a simulation, or restore after a crash
    • Undo an Event if issues come up from unexpected behavior in-game

    ❓ For What Purpose:



*/