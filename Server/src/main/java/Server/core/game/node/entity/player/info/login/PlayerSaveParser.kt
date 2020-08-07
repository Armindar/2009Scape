package core.game.node.entity.player.info.login

import core.game.node.entity.combat.CombatSpell
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.IronmanMode
import core.game.node.entity.player.link.SpellBookManager
import core.game.node.entity.player.link.emote.Emotes
import core.game.node.entity.player.link.grave.GraveType
import core.game.node.entity.player.link.music.MusicEntry
import core.game.node.entity.state.EntityState
import core.game.system.SystemLogger
import core.game.world.map.Location
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import plugin.ame.AntiMacroEvent
import plugin.ame.AntiMacroHandler
import plugin.interaction.item.brawling_gloves.BrawlingGloves
import java.io.FileReader

class PlayerSaveParser(val player: Player) {
    var parser = JSONParser()
    var reader: FileReader? = FileReader("data/players/${player.name.toLowerCase()}.json")
    var saveFile: JSONObject? = null
    var read = true

    init {
        reader ?: SystemLogger.log("Couldn't find save file for ${player.name}, or save is corrupted.").also { read = false }
        if(read){
            saveFile = parser.parse(reader) as JSONObject
        }
    }

    fun parse() {
        if(read) {
            parseCore()
            parseSkills()
            parseSettings()
            parseSlayer()
            parseQuests()
            parseAppearance()
            parseGrave()
            parseSpellbook()
            parseGrandExchange()
            parseSavedData()
            //TODO: PARSE PREVIOUS COMMUNICATION INFO (FOR SOME FUCKING REASON XD)
            parseAutocastSpell()
            parseFarming()
            parseConfigs()
            parseMonitor()
            parseMusic()
            parseFamiliars()
            parseBarCrawl()
            parseStates()
            parseAntiMacro()
            parseTT()
            parseBankPin()
            parseHouse()
            parseAchievements()
            parseIronman()
            parseEmoteManager()
            parseStatistics()
            parseBrawlingGloves()
        }
    }

    fun parseBrawlingGloves() {
        if(saveFile!!.containsKey("brawlingGloves")){
            val bgData: JSONArray = saveFile!!["brawlingGloves"] as JSONArray
            for(bg in bgData){
                val glove = bg as JSONObject
                player.brawlingGlovesManager.registerGlove(BrawlingGloves.forIndicator(glove.get("gloveId") as Byte).id, glove.get("charges") as Int)
            }
        }
    }

    fun parseStatistics(){
        if(saveFile!!.containsKey("statistics")){
            val stats: JSONArray = saveFile!!["statistics"] as JSONArray
            for(stat in stats){
                val s = stat as JSONObject
                val index = (s.get("index") as String).toInt()
                val value = (s.get("value") as String).toInt()
                player.statisticsManager.statistics.get(index).statisticalAmount = value
            }
        }
    }

    fun parseEmoteManager(){
        if(saveFile!!.containsKey("emoteData")){
            val emoteData: JSONArray = saveFile!!["emoteData"] as JSONArray
            for(emote in emoteData){
                val e = Emotes.values()[(emote as String).toInt()]
                if(!player.emoteManager.emotes.contains(e)){
                    player.emoteManager.emotes.add(e)
                }
            }
        }
    }

    fun parseIronman() {
        if(saveFile!!.containsKey("ironManMode")){
            val ironmanMode = (saveFile!!["ironManMode"] as String).toInt()
            player.ironmanManager.mode = IronmanMode.values()[ironmanMode]
        }
    }

    fun parseAchievements(){
        val achvData = saveFile!!["achievementData"] as JSONArray
        player.achievementDiaryManager.parse(achvData)
    }

    fun parseHouse(){
        val houseData = saveFile!!["houseData"] as JSONObject
        player.houseManager.parse(houseData)
    }

    fun parseBankPin(){
        val bpData = saveFile!!["bankPinManager"] as JSONObject
        player.bankPinManager.parse(bpData)
    }

    fun parseTT(){
        val ttData = saveFile!!["treasureTrails"] as JSONObject
        player.treasureTrailManager.parse(ttData)
    }

    fun parseAntiMacro(){
        if(saveFile!!.containsKey("antiMacroEvent")){
            val event: JSONObject = saveFile!!["antiMacroEvent"] as JSONObject
            val ame = AntiMacroHandler.EVENTS.get(event.get("eventName") as String)
            ame?.create(player)
        }
    }

    fun parseStates(){
        if(saveFile!!.containsKey("states")){
            val states: JSONArray = saveFile!!["states"] as JSONArray
            for(state in states){
                val s = state as JSONObject
                val stateId = (s.get("stateId") as String).toInt()
                val active = s.get("isActive") as Boolean
                if(active)
                    player.stateManager.register(EntityState.values()[stateId],true)
            }
        }
    }

    fun parseBarCrawl(){
        val barCrawlData = saveFile!!["barCrawl"] as JSONObject
        player.barcrawlManager.parse(barCrawlData)
    }

    fun parseFamiliars(){
        val familiarData = saveFile!!["familiarManager"] as JSONObject
        player.familiarManager.parse(familiarData)
    }

    fun parseMusic(){
        val unlockedSongs = saveFile!!["unlockedMusic"] as JSONArray
        for(song in unlockedSongs){
            val s = (song as String).toInt()
            val entry = MusicEntry.forId(s)
            player.musicPlayer.unlocked.put(entry.index,entry)
        }
    }

    fun parseMonitor(){
        val monitorData = saveFile!!["playerMonitor"] as JSONObject
        if(monitorData.containsKey("duplicationFlag")){
            val duplicationFlag: Int = (monitorData.get("duplicationFlag") as String).toInt()
            player.monitor.duplicationLog.flag(duplicationFlag)
        }
        if(monitorData.containsKey("macroFlag")){
            val macroFlag: Int = (monitorData.get("macroFlag") as String).toInt()
            player.monitor.macroFlag = macroFlag
        }
        if(monitorData.containsKey("lastIncreaseFlag")){
            val lastIncreaseFlag: Long = (monitorData.get("lastIncreaseFlag") as String).toLong()
            player.monitor.duplicationLog.lastIncreaseFlag = lastIncreaseFlag
        }
    }

    fun parseConfigs(){
        val configs = saveFile!!["configs"] as JSONArray
        for(config in configs){
            val c = config as JSONObject
            val index = (c.get("index") as String).toInt()
            val value = (c.get("value") as String).toInt()
            player.configManager.savedConfigurations[index] = value
        }
    }

    fun parseFarming(){
        val farmingData = saveFile!!["farming"] as JSONObject
        if(farmingData.containsKey("equipment")){
            val equipmentData: JSONArray? = farmingData.get("equipment") as JSONArray
            player.farmingManager.equipment.container.parse(equipmentData)
        }
        if(farmingData.containsKey("bins")){
            val compostData: JSONArray? = farmingData.get("bins") as JSONArray
            player.farmingManager.compostManager.parse(compostData)
        }
        if(farmingData.containsKey("wrappers")){
            val wrapperData: JSONArray? = farmingData.get("wrappers") as JSONArray
            player.farmingManager.parseWrappers(wrapperData)
        }
    }

    fun parseAutocastSpell(){
        val autocastRaw = saveFile!!["autocastSpell"]
        autocastRaw ?: return
        val autocast = autocastRaw as JSONObject
        val book = (autocast.get("book") as String).toInt()
        val spellId = (autocast.get("spellId") as String).toInt()
        player.properties.autocastSpell = SpellBookManager.SpellBook.values()[book].getSpell(spellId) as CombatSpell
    }

    fun parseSavedData(){
        val activityData = saveFile!!["activityData"] as JSONObject
        val questData = saveFile!!["questData"] as JSONObject
        val globalData = saveFile!!["globalData"] as JSONObject
        player.savedData.activityData.parse(activityData)
        player.savedData.questData.parse(questData)
        player.savedData.globalData.parse(globalData)
    }

    fun parseGrandExchange(){
        val geData: Any? = saveFile!!["grand_exchange"]
        var ge: JSONObject ?= null
        if(geData != null){ge = geData as JSONObject; player.grandExchange.parse(ge)}

    }

    fun parseSpellbook(){
        val spellbookData = (saveFile!!["spellbook"] as String).toInt()
        player.spellBookManager.setSpellBook(SpellBookManager.SpellBook.forInterface(spellbookData))
    }

    fun parseGrave() {
        saveFile?: return
        val graveData = (saveFile!!["grave_type"] as String ).toInt()
        player.graveManager.type = GraveType.values()[graveData]
    }

    fun parseAppearance(){
        saveFile ?: return
        val appearanceData = saveFile!!["appearance"] as JSONObject
        player.appearance.parse(appearanceData)
    }

    fun parseQuests() {
        saveFile ?: return
        val questData = saveFile!!["quests"] as JSONObject
        player.questRepository.parse(questData)
    }

    fun parseSlayer() {
        saveFile ?: return
        val slayerData = saveFile!!["slayer"] as JSONObject
        player.slayer.parse(slayerData)
    }

    fun parseCore(){
        saveFile ?: return
        val coreData = saveFile!!["core_data"] as JSONObject
        val inventory = coreData["inventory"] as JSONArray
        val bank = coreData["bank"] as JSONArray
        val equipment = coreData["equipment"] as JSONArray
        val location = coreData["location"] as String
        val loctokens = location.toString().split(",").map { it -> it.toInt() }
        val loc = Location(loctokens[0],loctokens[1],loctokens[2])
        player.inventory.parse(inventory)
        player.bank.parse(bank)
        player.equipment.parse(equipment)
        player.location = loc
    }

    fun parseSkills() {
        saveFile ?: return
        val skillData = saveFile!!["skills"] as JSONArray
        player.skills.parse(skillData)
        player.skills.experienceGained = saveFile!!["totalEXP"].toString().toDouble()
        player.skills.experienceMutiplier = saveFile!!["exp_multiplier"].toString().toDouble()
        if(saveFile!!.containsKey("milestone")){
            val milestone: JSONObject = saveFile!!["milestone"] as JSONObject
            player.skills.combatMilestone = (milestone.get("combatMilestone")).toString().toInt()
            player.skills.skillMilestone = (milestone.get("skillMilestone")).toString().toInt()
        }
    }

    fun parseSettings() {
        saveFile ?: return
        val settingsData = saveFile!!["settings"] as JSONObject
        player.settings.parse(settingsData)
    }


}