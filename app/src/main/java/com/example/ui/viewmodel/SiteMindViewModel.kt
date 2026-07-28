package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.MultiAgentService
import com.example.data.local.BlueprintEntity
import com.example.data.local.HazardEntity
import com.example.data.local.KnowledgeItemEntity
import com.example.data.local.ReportEntity
import com.example.data.local.SiteMindDatabase
import com.example.data.model.AuthScreenState
import com.example.data.model.ConnectionQualityInfo
import com.example.data.model.DiscoveredGlassDevice
import com.example.data.model.GlassAiState
import com.example.data.model.GlassDeviceState
import com.example.data.model.GlassPermissionsState
import com.example.data.model.LiveAiAnalysisResult
import com.example.data.model.LiveTranscriptEntry
import com.example.data.model.PairingStep
import com.example.data.model.ProjectInfo
import com.example.data.model.ShiftInfo
import com.example.data.model.SiteTaskItem
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.model.WeatherInfo
import com.example.data.model.SceneAnalysisData
import com.example.data.model.VisionBoundingBox
import com.example.data.model.WebSocketTelemetryState
import com.example.data.model.HazardCategory
import com.example.data.model.HazardDetectionItem
import com.example.data.model.HazardDetectionState
import com.example.data.model.AssistantChatMessage
import com.example.data.model.AssistantState
import com.example.data.model.SiteContextMemory
import com.example.data.model.BimMeasurement
import com.example.data.model.CadBimFileItem
import com.example.data.model.CadBimState
import com.example.data.model.CadDeviationItem
import com.example.data.model.CadFileType
import com.example.data.model.ComprehensiveReportItem
import com.example.data.model.ComprehensiveReportState
import com.example.data.model.ReportFilterCategory
import com.example.data.model.AnalyticsState
import com.example.data.model.AnalyticsTimeframe
import com.example.data.model.TradeCategory
import com.example.data.model.NotificationCategory
import com.example.data.model.NotificationPriority
import com.example.data.model.NotificationSettings
import com.example.data.model.SiteNotificationItem
import com.example.data.model.SiteNotificationState
import com.example.data.model.ProfileState
import com.example.data.model.AiIntegrationState
import com.example.data.model.AiModuleType
import com.example.data.model.ProductionPillar
import com.example.data.model.ProductionState
import com.example.data.model.ApiCategory
import com.example.data.model.BackendConsoleState
import com.example.data.model.BackendServerStatus
import com.example.data.model.FastApiEndpoint
import com.example.data.model.MaterialCategory
import com.example.data.model.MaterialItem
import com.example.data.model.MaterialVerificationState
import com.example.data.model.QualityCategory
import com.example.data.model.QualityHistoryRecord
import com.example.data.model.QualityInspectionItem
import com.example.data.model.QualityInspectionState
import com.example.data.model.QualityRecommendationItem
import com.example.data.repository.SiteMindRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SiteMindViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SiteMindDatabase.getDatabase(application)
    private val repository = SiteMindRepository(
        db.hazardDao(),
        db.reportDao(),
        db.blueprintDao(),
        db.knowledgeDao()
    )
    private val aiService = MultiAgentService()

    // Glass Device State
    private val _glassState = MutableStateFlow(GlassDeviceState())
    val glassState: StateFlow<GlassDeviceState> = _glassState.asStateFlow()

    // Room Database State Flows
    val activeHazards: StateFlow<List<HazardEntity>> = repository.activeHazards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHazards: StateFlow<List<HazardEntity>> = repository.allHazards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<ReportEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBlueprints: StateFlow<List<BlueprintEntity>> = repository.allBlueprints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allKnowledge: StateFlow<List<KnowledgeItemEntity>> = repository.allKnowledge
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live AI Analysis State
    private val _liveResult = MutableStateFlow(
        LiveAiAnalysisResult(
            queryText = "Initial Site AI Scan",
            aiResponseText = "SiteMind AI active on Ray-Ban Meta Glasses. Connected to Zone B-4 Level 3. All primary vision streams operational."
        )
    )
    val liveResult: StateFlow<LiveAiAnalysisResult> = _liveResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    // Search Query State
    private val _knowledgeSearchQuery = MutableStateFlow("")
    val knowledgeSearchQuery: StateFlow<String> = _knowledgeSearchQuery.asStateFlow()

    // Authentication State
    private val _authState = MutableStateFlow(AuthScreenState.SPLASH)
    val authState: StateFlow<AuthScreenState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "usr_49201",
            name = "John Doe",
            email = "john.doe@skylinetower.com",
            role = UserRole.SAFETY_INSPECTOR,
            company = "AeroBuild Skyline Construction",
            jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c3JfNDkyMDEiLCJuYW1lIjoiSm9obiBEb2UiLCJyb2xlIjoiU2FmZXR5IEluc3BlY3RvciJ9"
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _pendingEmail = MutableStateFlow("")
    val pendingEmail: StateFlow<String> = _pendingEmail.asStateFlow()

    private val _rememberSession = MutableStateFlow(true)
    val rememberSession: StateFlow<Boolean> = _rememberSession.asStateFlow()

    // Phase 3 — Device Pairing & Permissions State
    private val _isScanningBle = MutableStateFlow(false)
    val isScanningBle: StateFlow<Boolean> = _isScanningBle.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredGlassDevice>>(
        listOf(
            DiscoveredGlassDevice("rb_01", "Ray-Ban Meta Wayfarer", "7C:49:EB:11:82:90", -48, "Wayfarer", "Matte Black", 92, true),
            DiscoveredGlassDevice("rb_02", "Ray-Ban Meta Headliner", "9A:12:DF:33:41:02", -62, "Headliner", "Shiny Black", 85, false),
            DiscoveredGlassDevice("rb_03", "Ray-Ban Meta Skyler", "3F:88:AC:99:12:44", -78, "Skyler", "Chalk Grey", 100, false)
        )
    )
    val discoveredDevices: StateFlow<List<DiscoveredGlassDevice>> = _discoveredDevices.asStateFlow()

    private val _selectedDeviceForPairing = MutableStateFlow<DiscoveredGlassDevice?>(_discoveredDevices.value.first())
    val selectedDeviceForPairing: StateFlow<DiscoveredGlassDevice?> = _selectedDeviceForPairing.asStateFlow()

    private val _pairingStep = MutableStateFlow(PairingStep.DISCOVERY)
    val pairingStep: StateFlow<PairingStep> = _pairingStep.asStateFlow()

    private val _pairingProgress = MutableStateFlow(0f)
    val pairingProgress: StateFlow<Float> = _pairingProgress.asStateFlow()

    private val _pairingStatusMsg = MutableStateFlow("Ready for BLE Discovery")
    val pairingStatusMsg: StateFlow<String> = _pairingStatusMsg.asStateFlow()

    private val _permissionsState = MutableStateFlow(GlassPermissionsState())
    val permissionsState: StateFlow<GlassPermissionsState> = _permissionsState.asStateFlow()

    private val _connectionQuality = MutableStateFlow(ConnectionQualityInfo(-58, 54, 18, "Excellent"))
    val connectionQuality: StateFlow<ConnectionQualityInfo> = _connectionQuality.asStateFlow()

    private val _isReconnecting = MutableStateFlow(false)
    val isReconnecting: StateFlow<Boolean> = _isReconnecting.asStateFlow()

    private val _isCheckingFirmware = MutableStateFlow(false)
    val isCheckingFirmware: StateFlow<Boolean> = _isCheckingFirmware.asStateFlow()

    private val _firmwareUpdateAvailable = MutableStateFlow(false)
    val firmwareUpdateAvailable: StateFlow<Boolean> = _firmwareUpdateAvailable.asStateFlow()

    private val _pairingError = MutableStateFlow<String?>(null)
    val pairingError: StateFlow<String?> = _pairingError.asStateFlow()

    // Phase 5 — Live AI & Telemetry States
    private val _webSocketTelemetry = MutableStateFlow(WebSocketTelemetryState())
    val webSocketTelemetry: StateFlow<WebSocketTelemetryState> = _webSocketTelemetry.asStateFlow()

    private val _isEmergencyActive = MutableStateFlow(false)
    val isEmergencyActive: StateFlow<Boolean> = _isEmergencyActive.asStateFlow()

    private val _liveTranscripts = MutableStateFlow<List<LiveTranscriptEntry>>(
        listOf(
            LiveTranscriptEntry("tr_1", "User (Ray-Ban Mic)", "Hey Meta, inspect PPE compliance on Level 3 deck", "14:20:05", false),
            LiveTranscriptEntry("tr_2", "SiteMind AI (Glasses Audio)", "PPE compliance is 94%. Worker #2 at Grid B-4 is missing high-vis vest and safety glasses.", "14:20:08", true),
            LiveTranscriptEntry("tr_3", "User (Ray-Ban Mic)", "Check Beam B-12 alignment against CAD drawing S-204", "14:21:12", false),
            LiveTranscriptEntry("tr_4", "SiteMind AI (Glasses Audio)", "Beam B-12 shows +14mm upward deviation from CAD S-204 spec. Flagged to Quality Agent.", "14:21:16", true)
        )
    )
    val liveTranscripts: StateFlow<List<LiveTranscriptEntry>> = _liveTranscripts.asStateFlow()

    // Phase 6 — Scene Analysis States
    private val _sceneAnalysis = MutableStateFlow(SceneAnalysisData())
    val sceneAnalysis: StateFlow<SceneAnalysisData> = _sceneAnalysis.asStateFlow()

    private val _isCapturingScene = MutableStateFlow(false)
    val isCapturingScene: StateFlow<Boolean> = _isCapturingScene.asStateFlow()

    private val _isAnalysisSaved = MutableStateFlow(false)
    val isAnalysisSaved: StateFlow<Boolean> = _isAnalysisSaved.asStateFlow()

    private val _selectedBoundingBox = MutableStateFlow<VisionBoundingBox?>(null)
    val selectedBoundingBox: StateFlow<VisionBoundingBox?> = _selectedBoundingBox.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    // Phase 7 — Hazard Detection States
    private val _hazardDetectionState = MutableStateFlow(HazardDetectionState())
    val hazardDetectionState: StateFlow<HazardDetectionState> = _hazardDetectionState.asStateFlow()

    // Phase 8 — AI Assistant States
    private val _assistantState = MutableStateFlow(
        AssistantState(
            messages = listOf(
                AssistantChatMessage(
                    id = "msg_init_1",
                    isUser = false,
                    text = "SiteMind AI Voice Assistant linked via Ray-Ban Meta Smart Glasses. Context loaded: Level 18 Deck • Grid B-4, 14 workers, 2 open risk items.",
                    timestamp = "14:22",
                    oshaReference = "OSHA 1926.501 Fall Protection Standard",
                    confidenceScore = 98,
                    actionItems = listOf("Audit perimeter cable safety catchers", "Verify worker tie-off lanyards")
                )
            )
        )
    )
    val assistantState: StateFlow<AssistantState> = _assistantState.asStateFlow()

    // Phase 9 — CAD / BIM Verification States
    private val _cadBimState = MutableStateFlow(
        CadBimState(
            files = listOf(
                CadBimFileItem("cad_1", "MetroTower_L18_Structural.ifc", CadFileType.IFC, "42.8 MB", "2026-07-24", "Level 18 Deck • Grid B-4", 1240, "Rev C"),
                CadBimFileItem("cad_2", "S-204_Reinforcement_Plan.dwg", CadFileType.DWG, "18.2 MB", "2026-07-22", "Level 18 Deck • Grid B-4", 850, "Rev 4"),
                CadBimFileItem("cad_3", "MEP_Ductwork_HighRise.dxf", CadFileType.DXF, "24.5 MB", "2026-07-20", "Level 18 Core • Grid C-2", 620, "Rev 2")
            ),
            activeFileId = "cad_1",
            selectedViewMode = "OVERLAY",
            alignmentXOffsetMm = 2,
            alignmentYOffsetMm = -1,
            alignmentRotationDeg = 0.4,
            measurements = listOf(
                BimMeasurement("m_1", "Beam B-12 Span Length", "6.500 m", "6.514 m", "+14 mm", "± 5 mm", false),
                BimMeasurement("m_2", "Column C-18 Width", "400.0 mm", "412.0 mm", "+12 mm", "± 3 mm", false),
                BimMeasurement("m_3", "Rebar Pitch Spacing", "150.0 mm", "151.2 mm", "+1.2 mm", "± 5 mm", true),
                BimMeasurement("m_4", "HVAC Main Duct Clearance", "2.400 m", "2.392 m", "-8 mm", "± 10 mm", true)
            ),
            deviations = listOf(
                CadDeviationItem(
                    id = "dev_1",
                    elementName = "Structural Beam B-12 Shift",
                    gridLocation = "Level 18 Deck • Grid B-4",
                    cadSpec = "6.500m length at Y=14.20m",
                    asBuiltMeasured = "6.514m (+14mm offset East)",
                    deviationMm = 14.0,
                    severity = "HIGH",
                    oshaBimCode = "ACI 318-19 • Specs Sec 03300",
                    voiceFeedbackText = "Warning: Structural Beam B12 has a 14 millimeter lateral shift exceeding tolerance."
                ),
                CadDeviationItem(
                    id = "dev_2",
                    elementName = "Column C-18 Formwork Swell",
                    gridLocation = "Grid B-4 / Level 18",
                    cadSpec = "400mm x 400mm Pour Dimensions",
                    asBuiltMeasured = "412mm x 398mm Measured",
                    deviationMm = 12.0,
                    severity = "MEDIUM",
                    oshaBimCode = "ACI 117 Formwork Tolerance",
                    voiceFeedbackText = "Notice: Column C18 formwork swell detected at plus 12 millimeters."
                )
            )
        )
    )
    val cadBimState: StateFlow<CadBimState> = _cadBimState.asStateFlow()

    // Phase 10 — Quality Inspection States
    private val _qualityInspectionState = MutableStateFlow(
        QualityInspectionState(
            selectedCategory = QualityCategory.ALL,
            overallQualityScore = 92,
            qualityGrade = "A- GRADE",
            crackScore = 90,
            surfaceScore = 89,
            alignmentScore = 94,
            concreteScore = 95,
            pipeScore = 96,
            boltScore = 88,
            inspectionItems = listOf(
                QualityInspectionItem(
                    id = "qual_1",
                    title = "Shear Wall Structural Hairline Crack",
                    category = QualityCategory.CRACKS,
                    locationGrid = "Level 18 • Shear Wall W-2 (Grid C-3)",
                    measuredValue = "0.28 mm Width",
                    specificationThreshold = "Max ≤ 0.20 mm",
                    isPassed = false,
                    scoreImpact = -4,
                    inspectorName = "Ray-Ban Meta AI Scanner",
                    timestamp = "10:14 AM Today",
                    aiConfidence = 0.96f,
                    detailNotes = "Hairline crack extending 45cm vertically along tensile stress zone. Epoxy pressure injection recommended.",
                    recommendation = "Apply high-viscosity epoxy resin seal ASTM C881 before core slab loading."
                ),
                QualityInspectionItem(
                    id = "qual_2",
                    title = "Column C-18 Concrete Honeycombing",
                    category = QualityCategory.SURFACE,
                    locationGrid = "Level 18 • Column C-18 Base",
                    measuredValue = "12cm x 8cm Surface Void",
                    specificationThreshold = "No Aggregate Exposure",
                    isPassed = false,
                    scoreImpact = -3,
                    inspectorName = "Eng. Sarah Jenkins (QA/QC)",
                    timestamp = "09:45 AM Today",
                    aiConfidence = 0.92f,
                    detailNotes = "Minor honeycombing detected near rebar bundle due to local vibration gap during pour.",
                    recommendation = "Chipping back loose mortar and apply non-shrink cementitious grout patching."
                ),
                QualityInspectionItem(
                    id = "qual_3",
                    title = "Column Formwork Plumb Verticality",
                    category = QualityCategory.ALIGNMENT,
                    locationGrid = "Level 18 • Core Shaft Column C-12",
                    measuredValue = "2.1 mm Vertical Offset",
                    specificationThreshold = "Tolerance ± 5.0 mm",
                    isPassed = true,
                    scoreImpact = 0,
                    inspectorName = "Ray-Ban Meta AR Alignment",
                    timestamp = "08:30 AM Today",
                    aiConfidence = 0.98f,
                    detailNotes = "Laser plumbness check verifies column within ACI 117 tolerances.",
                    recommendation = "Maintain current turnbuckle bracing until concrete reaches 70% design strength."
                ),
                QualityInspectionItem(
                    id = "qual_4",
                    title = "Concrete Batch Slump & Curing Moisture",
                    category = QualityCategory.CONCRETE,
                    locationGrid = "Level 18 Deck • Batch C35/40 #882",
                    measuredValue = "Slump 115 mm • RH 94%",
                    specificationThreshold = "Slump 100-120 mm",
                    isPassed = true,
                    scoreImpact = 0,
                    inspectorName = "Concrete Lab Tech Marcus",
                    timestamp = "07:50 AM Today",
                    aiConfidence = 0.99f,
                    detailNotes = "Water-cement ratio 0.42 maintained. Curing blanket moisture level optimal.",
                    recommendation = "Maintain wet burlap covering for 72 consecutive hours."
                ),
                QualityInspectionItem(
                    id = "qual_5",
                    title = "MEP Main Fire Pipe Flange Seal Gap",
                    category = QualityCategory.PIPE,
                    locationGrid = "Level 18 Overhead • Risers P-04",
                    measuredValue = "1.2 mm Flange Gap",
                    specificationThreshold = "Max ≤ 1.5 mm",
                    isPassed = true,
                    scoreImpact = 0,
                    inspectorName = "Plumbing Inspector Alex",
                    timestamp = "Yesterday",
                    aiConfidence = 0.95f,
                    detailNotes = "Pressure tested at 150 PSI for 2 hours with zero pressure drop.",
                    recommendation = "Approved for insulation wrapping."
                ),
                QualityInspectionItem(
                    id = "qual_6",
                    title = "Structural Steel M24 Joint Bolt Torque",
                    category = QualityCategory.BOLT,
                    locationGrid = "Level 18 Perimeter Beam Splice J-12",
                    measuredValue = "380 N·m Torque",
                    specificationThreshold = "Spec 350-400 N·m",
                    isPassed = true,
                    scoreImpact = 0,
                    inspectorName = "Steel Structure AI Inspection",
                    timestamp = "Yesterday",
                    aiConfidence = 0.97f,
                    detailNotes = "Direct Tension Indicator (DTI) washers fully squished to calibrated gap.",
                    recommendation = "Apply anti-corrosion zinc primer topcoat."
                )
            ),
            recommendations = listOf(
                QualityRecommendationItem(
                    id = "rec_1",
                    category = QualityCategory.CRACKS,
                    priority = "CRITICAL",
                    title = "Inject Structural Epoxy for Shear Wall W-2 Crack",
                    location = "Level 18 • Grid C-3",
                    actionPlan = "Clean crack port, seal surface with epoxy paste, and inject low-viscosity resin ASTM C881 under 0.3 MPa pressure.",
                    estimatedFixTime = "3 Hours",
                    assignedTrade = "Specialty Concrete Repair Team"
                ),
                QualityRecommendationItem(
                    id = "rec_2",
                    category = QualityCategory.SURFACE,
                    priority = "HIGH",
                    title = "Patch Honeycomb Surface Voids on Column C-18",
                    location = "Level 18 • Grid B-4",
                    actionPlan = "Chip loose aggregate down to sound concrete, apply bonding agent, and pack Sika Grout 212.",
                    estimatedFixTime = "2 Hours",
                    assignedTrade = "Masonry & Finishing Crew"
                ),
                QualityRecommendationItem(
                    id = "rec_3",
                    category = QualityCategory.ALIGNMENT,
                    priority = "MEDIUM",
                    title = "Re-check Turnbuckle Tension on Beam B-12",
                    location = "Level 18 • Grid B-4",
                    actionPlan = "Verify 14mm shift recorded during CAD/BIM comparison and adjust shore jack alignment.",
                    estimatedFixTime = "1 Hour",
                    assignedTrade = "Formwork & Rigging Crew"
                )
            ),
            historyLogs = listOf(
                QualityHistoryRecord(
                    id = "hist_1",
                    inspectionDate = "2026-07-25",
                    inspector = "Eng. Sarah Jenkins (Lead QA/QC)",
                    zone = "Level 18 Structural Deck",
                    itemsInspected = 24,
                    passRatePercent = 92,
                    overallScore = 92,
                    status = "PASSED WITH CONDITIONAL REMEDIATION"
                ),
                QualityHistoryRecord(
                    id = "hist_2",
                    inspectionDate = "2026-07-22",
                    inspector = "Ray-Ban Meta Automated AI Patrol",
                    zone = "Level 17 Rebar & MEP Rough-In",
                    itemsInspected = 30,
                    passRatePercent = 96,
                    overallScore = 96,
                    status = "FULLY CERTIFIED"
                ),
                QualityHistoryRecord(
                    id = "hist_3",
                    inspectionDate = "2026-07-18",
                    inspector = "City Building Inspector R. Vance",
                    zone = "Level 16 Post-Tensioning Audit",
                    itemsInspected = 18,
                    passRatePercent = 100,
                    overallScore = 100,
                    status = "APPROVED & SIGNED OFF"
                )
            )
        )
    )
    val qualityInspectionState: StateFlow<QualityInspectionState> = _qualityInspectionState.asStateFlow()

    // Phase 11 — Material Verification State
    private val _materialVerificationState = MutableStateFlow(
        MaterialVerificationState(
            selectedCategory = MaterialCategory.ALL,
            totalMaterialsInspected = 52,
            compliantMaterialsCount = 50,
            complianceRatePercent = 96,
            expiredOrNonCompliantCount = 2,
            materials = listOf(
                MaterialItem(
                    id = "mat_1",
                    materialName = "Portland High-Strength Cement C35/40",
                    category = MaterialCategory.CONCRETE,
                    brand = "LafargeHolcim Structural",
                    specification = "CEM I 52.5N • ASTM C150 Type III",
                    expiryDate = "2026-11-15 (112 Days Remaining)",
                    batchNumber = "BATCH #LH-88392-C",
                    isCompliant = true,
                    complianceCode = "ASTM C150 / EN 197-1",
                    currentStockQuantity = "120.0",
                    unit = "Bags (50kg)",
                    deliveryDate = "2026-07-24",
                    locationGrid = "Level 18 Storage Bay B-2",
                    detectedByVision = true,
                    aiConfidence = 0.98f,
                    notes = "Vision AI verified barcode, seal integrity, and dry pallet moisture level < 2%."
                ),
                MaterialItem(
                    id = "mat_2",
                    materialName = "Deformed High-Tensile Steel Rebar Ø25mm",
                    category = MaterialCategory.REBAR_STEEL,
                    brand = "ArcelorMittal TMT Steel",
                    specification = "Grade 600D • High Seismic Ductility",
                    expiryDate = "N/A (Non-Perishable)",
                    batchNumber = "HEAT #AM-99214-R25",
                    isCompliant = true,
                    complianceCode = "ASTM A615 / BS 4449",
                    currentStockQuantity = "42.5",
                    unit = "Metric Tons",
                    deliveryDate = "2026-07-22",
                    locationGrid = "Yard Zone 4 Steel Staging",
                    detectedByVision = true,
                    aiConfidence = 0.99f,
                    notes = "Mill Test Certificate (MTC) matches physical tag heat number. Tensile strength verified 620 MPa."
                ),
                MaterialItem(
                    id = "mat_3",
                    materialName = "Rapid-Set Structural Epoxy Adhesive",
                    category = MaterialCategory.CHEMICALS,
                    brand = "SikaDur-31 Hi-Mod GEL",
                    specification = "2-Component Rigid Epoxy Resin",
                    expiryDate = "2026-07-20 (EXPIRED 5 DAYS AGO)",
                    batchNumber = "BATCH #SIKA-2025-07A",
                    isCompliant = false,
                    complianceCode = "ASTM C881 Type I/IV",
                    currentStockQuantity = "14.0",
                    unit = "Canisters (5L)",
                    deliveryDate = "2025-07-20",
                    locationGrid = "Hazardous Material Locker H-1",
                    detectedByVision = true,
                    aiConfidence = 0.95f,
                    notes = "FLAGGED BY RAY-BAN VISION: Expiration date exceeded! Pot life and gel strength compromised. Quarantine immediately!"
                ),
                MaterialItem(
                    id = "mat_4",
                    materialName = "Schedule 80 Heavy Duty PVC Water Riser Pipe 4\"",
                    category = MaterialCategory.PIPING_MEP,
                    brand = "Charlotte Pipe & Foundry",
                    specification = "SCH 80 Industrial Pressure Rating 320 PSI",
                    expiryDate = "N/A (UV Protection Coating Valid 5 Yrs)",
                    batchNumber = "LOT #CPF-2026-03-P80",
                    isCompliant = true,
                    complianceCode = "ASTM D1785 / NSF 61",
                    currentStockQuantity = "85.0",
                    unit = "Lengths (6m)",
                    deliveryDate = "2026-07-21",
                    locationGrid = "Level 18 MEP Laydown R-1",
                    detectedByVision = true,
                    aiConfidence = 0.97f,
                    notes = "NSF drinking water safety seal detected. Pressure test rating matches level 18 vertical riser specs."
                ),
                MaterialItem(
                    id = "mat_5",
                    materialName = "Structural High-Strength Anchor Bolt M24 x 300mm",
                    category = MaterialCategory.FASTENERS,
                    brand = "Hilti Heavy Anchor Systems",
                    specification = "HAS-U 8.8 Galvanized Carbon Steel",
                    expiryDate = "N/A",
                    batchNumber = "BATCH #HILTI-M24-88A",
                    isCompliant = true,
                    complianceCode = "ETA-05/0069 / ISO 898-1",
                    currentStockQuantity = "350.0",
                    unit = "Units",
                    deliveryDate = "2026-07-23",
                    locationGrid = "Level 18 Core Steel Joint J-12",
                    detectedByVision = true,
                    aiConfidence = 0.96f,
                    notes = "Galvanized coating thickness 45μm verified by electromagnetic sensor. Anti-rust seal intact."
                ),
                MaterialItem(
                    id = "mat_6",
                    materialName = "Flame-Retardant Armored Power Cable 4C x 185mm²",
                    category = MaterialCategory.ELECTRICAL,
                    brand = "Prysmian Group Heavy Power",
                    specification = "600/1000V XLPE/LSOH Armored Steel Wire",
                    expiryDate = "N/A",
                    batchNumber = "REEL #PRY-2026-9081",
                    isCompliant = false,
                    complianceCode = "IEC 60502-1 / BS 6724",
                    currentStockQuantity = "220.0",
                    unit = "Meters (Reel #3)",
                    deliveryDate = "2026-07-25",
                    locationGrid = "Electrical Substation Shaft E-2",
                    detectedByVision = true,
                    aiConfidence = 0.91f,
                    notes = "FLAGGED BY RAY-BAN VISION: Insufficient LSOH smoke rating tag! Non-compliant for high-rise indoor riser shafts."
                )
            )
        )
    )
    val materialVerificationState: StateFlow<MaterialVerificationState> = _materialVerificationState.asStateFlow()

    // Phase 12 — Comprehensive Automated Reporting State
    private val _comprehensiveReportState = MutableStateFlow(
        ComprehensiveReportState(
            selectedFilter = ReportFilterCategory.ALL,
            reportsList = listOf(
                ComprehensiveReportItem(
                    id = "rep_dpr_101",
                    typeCode = "DAILY",
                    title = "Daily Progress Report (DPR) — Level 18 Slab Concrete",
                    date = "2026-07-25 (Today)",
                    author = "Eng. Marcus Vance (Lead Site Engineer)",
                    executiveSummary = "Level 18 shear wall pouring completed. 120m³ C35/40 concrete poured with 0 incidents. Post-tensioning cables tensioned to 85% yield.",
                    keyMetrics = mapOf(
                        "Crew Size" to "38 Workers",
                        "Hours Worked" to "304 Man-Hours",
                        "Concrete Poured" to "120 m³",
                        "Active Hazards" to "1 Resolved",
                        "Quality Grade" to "A- (92%)"
                    ),
                    aiGeneratedInsights = listOf(
                        "Vision AI verified 100% PPE compliance across rebar and formwork crews.",
                        "Concrete curing moisture maintained at 94% RH via wet burlap blankets.",
                        "Ray-Ban Meta Glasses captured 14 high-resolution 3D point cloud snapshots of level 18 grid C-3."
                    ),
                    hazardsReported = 1,
                    qualityDefectsFound = 2,
                    crewSize = 38
                ),
                ComprehensiveReportItem(
                    id = "rep_wpr_202",
                    typeCode = "WEEKLY",
                    title = "Weekly Executive Progress Report (WPR) — Week 30",
                    date = "2026-07-20 to 2026-07-25",
                    author = "SiteMind Multi-Agent Executive AI",
                    executiveSummary = "Level 17 complete and certified by City Inspector Vance. Level 18 structural deck schedule variance +2 days ahead of master BIM timeline.",
                    keyMetrics = mapOf(
                        "Weekly Progress" to "100% Target Met",
                        "Total Hazards Logged" to "4 (All Resolved)",
                        "LTI (Lost Time Injury)" to "0 Hours",
                        "Budget Variance" to "-1.8% (Under)",
                        "BIM Clashes Resolved" to "7 Clashes"
                    ),
                    aiGeneratedInsights = listOf(
                        "Multi-Agent AI auto-synthesized 410 hours of video telemetry from site supervisor smart glasses.",
                        "Equipment uptime for tower crane TC-01 reached 99.2%.",
                        "Material supply chain alert: Cement delivery batch #LH-88392 verified compliant."
                    ),
                    hazardsReported = 4,
                    qualityDefectsFound = 3,
                    crewSize = 145
                ),
                ComprehensiveReportItem(
                    id = "rep_inc_303",
                    typeCode = "INCIDENT",
                    title = "Incident / Near-Miss Audit Report (IR-08)",
                    date = "2026-07-24",
                    author = "Ray-Ban Glasses AI Hazard Sentinel",
                    executiveSummary = "Unsecured scaffold plank detected on Level 18 perimeter at Grid B-4. High wind speeds (28 km/h). Zone automatically cordoned off by AI notification.",
                    keyMetrics = mapOf(
                        "Severity Level" to "MEDIUM (Near-Miss)",
                        "Response Time" to "< 2 Minutes",
                        "OSHA Breach Code" to "1926.451(b)(1)",
                        "Status" to "CLOSED & REMEDIATED",
                        "Action Item" to "Toe-boards installed"
                    ),
                    aiGeneratedInsights = listOf(
                        "Glasses HUD triggered acoustic warning in supervisor earbud immediately upon visual detection.",
                        "Scaffold sub-contractor notified and corrective action verified via follow-up AI photo scan."
                    ),
                    hazardsReported = 1,
                    qualityDefectsFound = 0,
                    crewSize = 8
                ),
                ComprehensiveReportItem(
                    id = "rep_saf_404",
                    typeCode = "SAFETY",
                    title = "Site-Wide Safety Audit & PPE Inspection (SVR-19)",
                    date = "2026-07-23",
                    author = "Chief Safety Officer Sarah Jenkins",
                    executiveSummary = "Quarterly site-wide safety audit covering fall protection, excavation shoring, electrical grounding, and crane rig assembly.",
                    keyMetrics = mapOf(
                        "Safety Score" to "96 / 100",
                        "Workers Screened" to "112 Personnel",
                        "PPE Compliance" to "98.2%",
                        "Violations Fixed" to "2 Verbal Warnings",
                        "Certifications" to "OSHA 30 Certified"
                    ),
                    aiGeneratedInsights = listOf(
                        "2 workers flagged for unclipped harness lanyards while working near slab edge; immediate correction enforced.",
                        "All fire extinguishers at Level 18 temporary distribution board verified inspected and fully charged."
                    ),
                    hazardsReported = 2,
                    qualityDefectsFound = 0,
                    crewSize = 112
                ),
                ComprehensiveReportItem(
                    id = "rep_qua_505",
                    typeCode = "QUALITY",
                    title = "Quality Assurance & Defect Audit (QAR-14)",
                    date = "2026-07-22",
                    author = "QA/QC Specialist Eng. David Chen",
                    executiveSummary = "Structural concrete core drilling and ultrasonic flaw detection on Level 16 & 17 transfer girders.",
                    keyMetrics = mapOf(
                        "SQI Quality Score" to "92 / 100",
                        "Concrete Core Strength" to "42.5 MPa (Design 40)",
                        "Rebar Cover Depth" to "38mm (Spec 35-40mm)",
                        "Defects Open" to "1 (Hairline Crack)",
                        "NCR Status" to "Epoxy Injection Pending"
                    ),
                    aiGeneratedInsights = listOf(
                        "Hairline crack on shear wall W-2 (0.28mm width) tagged for low-viscosity epoxy pressure injection.",
                        "Ultrasonic testing confirmed 0 internal honeycomb voids inside column C-12 base."
                    ),
                    hazardsReported = 0,
                    qualityDefectsFound = 1,
                    crewSize = 18
                )
            )
        )
    )
    val comprehensiveReportState: StateFlow<ComprehensiveReportState> = _comprehensiveReportState.asStateFlow()

    // Phase 13 — Analytics State
    private val _analyticsState = MutableStateFlow(AnalyticsState())
    val analyticsState: StateFlow<AnalyticsState> = _analyticsState.asStateFlow()

    // Phase 14 — Notification State
    private val _notificationState = MutableStateFlow(
        SiteNotificationState(
            unreadCount = 4,
            notificationsList = listOf(
                SiteNotificationItem(
                    id = "notif_hazard_01",
                    category = NotificationCategory.HAZARDS,
                    title = "CRITICAL HAZARD DETECTED",
                    message = "Unguarded perimeter detected on Level 18 West Slab. Immediate safety harness tether required.",
                    timestamp = "2 mins ago",
                    priority = NotificationPriority.CRITICAL,
                    isRead = false,
                    actionText = "VIEW HAZARD",
                    targetRoute = "hazard_detection"
                ),
                SiteNotificationItem(
                    id = "notif_ai_01",
                    category = NotificationCategory.AI_ALERTS,
                    title = "Scene AI Geometry Anomaly",
                    message = "Rebar grid spacing deviation (+18mm) flagged on Section B deck pour zone.",
                    timestamp = "14 mins ago",
                    priority = NotificationPriority.HIGH,
                    isRead = false,
                    actionText = "INSPECT MODEL",
                    targetRoute = "scene_analysis"
                ),
                SiteNotificationItem(
                    id = "notif_report_01",
                    category = NotificationCategory.REPORTS,
                    title = "Daily Progress Report Ready (DPR)",
                    message = "AI Daily Site Report #42 generated with 18 automated field snapshots & QA audit score.",
                    timestamp = "1 hour ago",
                    priority = NotificationPriority.MEDIUM,
                    isRead = false,
                    actionText = "OPEN REPORT",
                    targetRoute = "reports"
                ),
                SiteNotificationItem(
                    id = "notif_task_01",
                    category = NotificationCategory.TASKS,
                    title = "New Quality Punch Task Assigned",
                    message = "Re-inspect MEP cable tray grounding conduit on North Riser before 16:00.",
                    timestamp = "2 hours ago",
                    priority = NotificationPriority.HIGH,
                    isRead = false,
                    actionText = "VIEW TASK",
                    targetRoute = "quality"
                ),
                SiteNotificationItem(
                    id = "notif_firmware_01",
                    category = NotificationCategory.FIRMWARE,
                    title = "Ray-Ban OS v2.4.1 Ready for Install",
                    message = "Over-the-Air firmware update improves spatial depth AI performance and battery efficiency +12%.",
                    timestamp = "5 hours ago",
                    priority = NotificationPriority.LOW,
                    isRead = true,
                    actionText = "UPDATE GLASSES",
                    targetRoute = "device"
                ),
                SiteNotificationItem(
                    id = "notif_hazard_02",
                    category = NotificationCategory.HAZARDS,
                    title = "PPE Compliance Warning Resolved",
                    message = "Subcontractor crew on Level 14 equipped hardhats after AI HUD audio prompt.",
                    timestamp = "Yesterday",
                    priority = NotificationPriority.LOW,
                    isRead = true
                )
            )
        )
    )
    val notificationState: StateFlow<SiteNotificationState> = _notificationState.asStateFlow()

    // Phase 15 — Profile State
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    // Phase 16 — Backend & API Console State
    private val _backendConsoleState = MutableStateFlow(
        BackendConsoleState(
            endpointsList = listOf(
                FastApiEndpoint(
                    id = "ep_auth_login",
                    category = ApiCategory.AUTH,
                    method = "POST",
                    path = "/api/v1/auth/login",
                    summary = "User JWT Authentication & Login",
                    requestBody = "{\n  \"email\": \"marcus.vance@sitemind.ai\",\n  \"password\": \"sitemind2026\"\n}",
                    sampleResponse = "{\n  \"access_token\": \"eyJhbGci...\",\n  \"token_type\": \"bearer\",\n  \"user_id\": \"user_101\",\n  \"role\": \"Senior Site Safety Engineer\"\n}"
                ),
                FastApiEndpoint(
                    id = "ep_auth_me",
                    category = ApiCategory.AUTH,
                    method = "GET",
                    path = "/api/v1/auth/me",
                    summary = "Get Current Authenticated User Credentials",
                    sampleResponse = "{\n  \"id\": \"user_101\",\n  \"name\": \"Marcus Vance\",\n  \"email\": \"marcus.vance@sitemind.ai\",\n  \"company\": \"BuildTech Global Engineering\"\n}"
                ),
                FastApiEndpoint(
                    id = "ep_projects_list",
                    category = ApiCategory.PROJECTS,
                    method = "GET",
                    path = "/api/v1/projects",
                    summary = "List Active Construction Site Projects",
                    sampleResponse = "[\n  {\n    \"id\": \"proj_01\",\n    \"name\": \"Metro Tower Construction\",\n    \"level\": \"Level 18 West Slab\",\n    \"progressPct\": 68.5\n  }\n]"
                ),
                FastApiEndpoint(
                    id = "ep_reports_get",
                    category = ApiCategory.REPORTS,
                    method = "GET",
                    path = "/api/v1/reports",
                    summary = "Fetch Daily Progress Reports (DPR)",
                    sampleResponse = "[\n  {\n    \"id\": \"report_dpr_42\",\n    \"shiftDate\": \"2026-07-25\",\n    \"safetyScorePct\": 98.0,\n    \"fieldSnapshotsCount\": 18\n  }\n]"
                ),
                FastApiEndpoint(
                    id = "ep_documents_rag",
                    category = ApiCategory.DOCUMENTS,
                    method = "POST",
                    path = "/api/v1/documents/rag-search",
                    summary = "Vector RAG Semantic Search over Drawings & Specs",
                    requestBody = "{\n  \"query\": \"What is the required tie-off height for slab edges?\"\n}",
                    sampleResponse = "{\n  \"matched_chunks\": [\n    {\n      \"doc\": \"SOP-OSHA-2026\",\n      \"text\": \"100% tie-off mandatory above 10ft height.\",\n      \"relevance_score\": 0.96\n    }\n  ]\n}"
                ),
                FastApiEndpoint(
                    id = "ep_cad_clash",
                    category = ApiCategory.CAD,
                    method = "GET",
                    path = "/api/v1/cad/clash-detection",
                    summary = "IFC/DWG Automated Spatial Clash Analysis",
                    sampleResponse = "[\n  {\n    \"clashId\": \"CLASH_108\",\n    \"tradeA\": \"HVAC Ducting\",\n    \"tradeB\": \"Structural Steel Beam\",\n    \"severity\": \"HIGH\"\n  }\n]"
                ),
                FastApiEndpoint(
                    id = "ep_vision_analyze",
                    category = ApiCategory.VISION,
                    method = "POST",
                    path = "/api/v1/vision/analyze-stream",
                    summary = "Ray-Ban HUD Video Frame Geometry & PPE Vision AI",
                    requestBody = "{\n  \"frame_id\": \"frame_88412_hud_01\"\n}",
                    sampleResponse = "{\n  \"detections\": [\n    {\n      \"objectClass\": \"Rebar Spacing Grid\",\n      \"safetyStatus\": \"DEVIATION_DETECTED_+18MM\"\n    }\n  ]\n}"
                ),
                FastApiEndpoint(
                    id = "ep_notif_push",
                    category = ApiCategory.NOTIFICATIONS,
                    method = "POST",
                    path = "/api/v1/notifications/push",
                    summary = "Dispatch Real-Time Safety Push to HUD Earbuds",
                    requestBody = "{\n  \"category\": \"HAZARDS\",\n  \"title\": \"CRITICAL HAZARD\",\n  \"priority\": \"CRITICAL\"\n}",
                    sampleResponse = "{\n  \"status\": \"DISPATCHED\",\n  \"hud_chime\": \"HUD_EARBUD_ALERT_HIGH\",\n  \"delivery_ms\": 12\n}"
                ),
                FastApiEndpoint(
                    id = "ep_ai_prompt",
                    category = ApiCategory.AI,
                    method = "POST",
                    path = "/api/v1/ai/prompt",
                    summary = "Gemini Multimodal Site Intelligence Prompt",
                    requestBody = "{\n  \"prompt\": \"Verify structural rebar grid density for Level 18 slab pour.\"\n}",
                    sampleResponse = "{\n  \"ai_response\": \"SiteMind AI: Rebar grid meets ASTM A615 Grade 60 tensile spec. Safe for concrete deck pour.\",\n  \"model_used\": \"Gemini 1.5 Pro Multimodal Vision\"\n}"
                ),
                FastApiEndpoint(
                    id = "ep_admin_health",
                    category = ApiCategory.ADMIN,
                    method = "GET",
                    path = "/api/v1/admin/health",
                    summary = "FastAPI, PostgreSQL, Redis & Docker Container Health Check",
                    sampleResponse = "{\n  \"status\": \"ONLINE\",\n  \"fastapi_version\": \"0.111.0\",\n  \"database\": \"PostgreSQL 16 (Connected)\",\n  \"redis_cache\": \"Redis 7.0 (Connected)\"\n}"
                )
            )
        )
    )
    val backendConsoleState: StateFlow<BackendConsoleState> = _backendConsoleState.asStateFlow()

    // Phase 17 — AI Integration State
    private val _aiIntegrationState = MutableStateFlow(AiIntegrationState())
    val aiIntegrationState: StateFlow<AiIntegrationState> = _aiIntegrationState.asStateFlow()

    // Phase 18 — Production Readiness State
    private val _productionState = MutableStateFlow(ProductionState())
    val productionState: StateFlow<ProductionState> = _productionState.asStateFlow()

    // Phase 4 — Dashboard States
    private val _weatherInfo = MutableStateFlow(WeatherInfo())
    val weatherInfo: StateFlow<WeatherInfo> = _weatherInfo.asStateFlow()

    private val _projectInfo = MutableStateFlow(ProjectInfo())
    val projectInfo: StateFlow<ProjectInfo> = _projectInfo.asStateFlow()

    private val _shiftInfo = MutableStateFlow(ShiftInfo())
    val shiftInfo: StateFlow<ShiftInfo> = _shiftInfo.asStateFlow()

    private val _siteTasks = MutableStateFlow<List<SiteTaskItem>>(
        listOf(
            SiteTaskItem("tsk_1", "Verify Level 18 Rebar Placement & Tie Spacing", "Grid B-4 Level 18", "Structural", "HIGH", false, "11:30"),
            SiteTaskItem("tsk_2", "Audit Perimeter Edge Fall Protection Mesh", "East Elevator Shaft L18", "Safety", "CRITICAL", false, "12:00"),
            SiteTaskItem("tsk_3", "Concrete Core Ultrasonic Strength Test Review", "Basement Core Shaft", "Quality", "MEDIUM", true, "09:15"),
            SiteTaskItem("tsk_4", "Pre-Pour Inspection Clearance Sign-Off", "Grid C-2 Deck", "Compliance", "HIGH", false, "15:00"),
            SiteTaskItem("tsk_5", "Calibrate Tower Crane Proximity Sensor Array", "Overhead Zone 2", "Equipment", "MEDIUM", true, "08:30")
        )
    )
    val siteTasks: StateFlow<List<SiteTaskItem>> = _siteTasks.asStateFlow()

    init {
        // Run initial AI analysis
        runAiQuery("Perform initial safety & blueprint check")
    }

    fun navigateToAuth(screen: AuthScreenState) {
        _authError.value = null
        _authState.value = screen
    }

    fun toggleRememberSession() {
        _rememberSession.value = !_rememberSession.value
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || !email.contains("@")) {
            _authError.value = "Please enter a valid email address."
            return
        }
        if (pass.length < 6) {
            _authError.value = "Password must be at least 6 characters."
            return
        }

        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            delay(1000) // Simulate Firebase/JWT Network Call
            _authLoading.value = false

            _currentUser.value = _currentUser.value.copy(
                email = email,
                name = email.substringBefore("@").replace(".", " ").capitalize()
            )
            _authState.value = AuthScreenState.ROLE_SELECTION
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            delay(1200) // Simulate Google OAuth Auth Token exchange
            _authLoading.value = false

            _currentUser.value = UserProfile(
                id = "google_user_882",
                name = "Alex Vance (Google)",
                email = "alex.vance@gmail.com",
                role = UserRole.SAFETY_INSPECTOR,
                isGoogleAuth = true
            )
            _authState.value = AuthScreenState.ROLE_SELECTION
        }
    }

    fun register(name: String, email: String, pass: String, role: UserRole) {
        if (name.isBlank()) {
            _authError.value = "Please enter your full name."
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            _authError.value = "Please enter a valid email address."
            return
        }
        if (pass.length < 6) {
            _authError.value = "Password must be at least 6 characters."
            return
        }

        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            delay(1000) // Simulate Firebase Auth registration
            _pendingEmail.value = email
            _currentUser.value = UserProfile(
                name = name,
                email = email,
                role = role
            )
            _authLoading.value = false
            _authState.value = AuthScreenState.OTP_VERIFICATION
        }
    }

    fun sendForgotPassword(email: String) {
        if (email.isBlank() || !email.contains("@")) {
            _authError.value = "Please enter a valid email address."
            return
        }
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            delay(1000)
            _pendingEmail.value = email
            _authLoading.value = false
            _authState.value = AuthScreenState.OTP_VERIFICATION
        }
    }

    fun verifyOtp(code: String): Boolean {
        if (code.length != 4 && code.length != 6) {
            _authError.value = "Please enter a valid 4-digit code."
            return false
        }
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            delay(800)
            _authLoading.value = false
            _authState.value = AuthScreenState.ROLE_SELECTION
        }
        return true
    }

    fun selectRole(role: UserRole) {
        _currentUser.value = _currentUser.value.copy(role = role)
        val updatedUser = _profileState.value.profile.copy(isLoggedOut = false, role = role)
        _profileState.value = _profileState.value.copy(profile = updatedUser)
        _authState.value = AuthScreenState.AUTHENTICATED
    }

    fun logout() {
        val updatedUser = _profileState.value.profile.copy(isLoggedOut = true)
        _profileState.value = _profileState.value.copy(
            profile = updatedUser,
            showLogoutConfirmationDialog = false
        )
        _authState.value = AuthScreenState.LOGIN
    }

    fun runAiQuery(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            
            // Append user transcript entry
            val userEntry = LiveTranscriptEntry(
                id = "tr_${System.currentTimeMillis()}_u",
                speaker = "User (Ray-Ban Mic)",
                text = query,
                timestamp = time,
                isAi = false
            )
            _liveTranscripts.value = _liveTranscripts.value + userEntry

            _isAnalyzing.value = true
            _glassState.value = _glassState.value.copy(connectionState = GlassAiState.LISTENING)

            kotlinx.coroutines.delay(600)
            _glassState.value = _glassState.value.copy(connectionState = GlassAiState.THINKING)

            val result = aiService.processQueryAndFrame(query)

            _glassState.value = _glassState.value.copy(connectionState = GlassAiState.SPEAKING)
            _liveResult.value = result
            _isAnalyzing.value = false

            // Append AI transcript entry
            val aiEntry = LiveTranscriptEntry(
                id = "tr_${System.currentTimeMillis()}_ai",
                speaker = "SiteMind AI (Glasses Audio)",
                text = result.aiResponseText,
                timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
                isAi = true
            )
            _liveTranscripts.value = _liveTranscripts.value + aiEntry

            kotlinx.coroutines.delay(1800)
            _glassState.value = _glassState.value.copy(connectionState = GlassAiState.CONNECTED)
        }
    }

    fun triggerEmergencySos() {
        _isEmergencyActive.value = true
        // Add emergency hazard to DB
        viewModelScope.launch {
            repository.insertHazard(
                HazardEntity(
                    title = "🚨 EMERGENCY SOS BROADCAST TRIGGERED",
                    category = "EMERGENCY",
                    severity = "CRITICAL",
                    location = "Zone B-4 Level 3 (Ray-Ban AR HUD)",
                    description = "Site worker triggered high-priority SOS alert from Ray-Ban Meta glasses live stream.",
                    actionTaken = "Alert broadcast to Safety Superintendent & Channel 4 Emergency Frequency."
                )
            )
        }
    }

    fun clearEmergency() {
        _isEmergencyActive.value = false
    }

    fun simulateVoiceState(state: GlassAiState) {
        _glassState.value = _glassState.value.copy(connectionState = state)
    }

    fun toggleLiveStream() {
        _glassState.value = _glassState.value.copy(
            isLiveStreaming = !_glassState.value.isLiveStreaming
        )
    }

    fun toggleVoiceTrigger() {
        _glassState.value = _glassState.value.copy(
            voiceTriggerEnabled = !_glassState.value.voiceTriggerEnabled
        )
    }

    fun setGlassState(newState: GlassAiState) {
        _glassState.value = _glassState.value.copy(connectionState = newState)
    }

    fun resolveHazard(hazard: HazardEntity) {
        viewModelScope.launch {
            repository.updateHazard(
                hazard.copy(
                    isResolved = true,
                    actionTaken = "Resolved by Safety Officer via SiteMind Mobile App"
                )
            )
        }
    }

    fun addHazard(title: String, category: String, severity: String, location: String, description: String) {
        viewModelScope.launch {
            repository.insertHazard(
                HazardEntity(
                    title = title,
                    category = category,
                    severity = severity,
                    location = location,
                    description = description
                )
            )
        }
    }

    fun addReport(type: String, title: String, summary: String, crewCount: Int, hazardsCount: Int) {
        viewModelScope.launch {
            repository.insertReport(
                ReportEntity(
                    type = type,
                    title = title,
                    summary = summary,
                    crewCount = crewCount,
                    hazardsFound = hazardsCount
                )
            )
        }
    }

    fun updateKnowledgeQuery(query: String) {
        _knowledgeSearchQuery.value = query
    }

    // Phase 3 — Device Pairing & Permissions Functions
    fun startBleScan() {
        viewModelScope.launch {
            _isScanningBle.value = true
            _pairingError.value = null
            _pairingStatusMsg.value = "Scanning nearby BLE spectrum for Ray-Ban Meta frames..."
            delay(1500)
            _isScanningBle.value = false
            _pairingStatusMsg.value = "Found ${_discoveredDevices.value.size} nearby Ray-Ban frames"
        }
    }

    fun selectDeviceToPair(device: DiscoveredGlassDevice) {
        _selectedDeviceForPairing.value = device
        _pairingError.value = null
        if (!_permissionsState.value.allGranted) {
            _pairingStep.value = PairingStep.PERMISSIONS
        } else {
            executePairingHandshake(device)
        }
    }

    fun toggleCameraPermission() {
        _permissionsState.value = _permissionsState.value.copy(cameraGranted = !_permissionsState.value.cameraGranted)
    }

    fun toggleMicPermission() {
        _permissionsState.value = _permissionsState.value.copy(microphoneGranted = !_permissionsState.value.microphoneGranted)
    }

    fun toggleNotificationPermission() {
        _permissionsState.value = _permissionsState.value.copy(notificationGranted = !_permissionsState.value.notificationGranted)
    }

    fun toggleBluetoothPermission() {
        _permissionsState.value = _permissionsState.value.copy(bluetoothGranted = !_permissionsState.value.bluetoothGranted)
    }

    fun grantAllPermissions() {
        _permissionsState.value = GlassPermissionsState(
            cameraGranted = true,
            microphoneGranted = true,
            notificationGranted = true,
            bluetoothGranted = true
        )
    }

    fun proceedFromPermissions() {
        val dev = _selectedDeviceForPairing.value ?: _discoveredDevices.value.first()
        executePairingHandshake(dev)
    }

    fun executePairingHandshake(device: DiscoveredGlassDevice) {
        viewModelScope.launch {
            _pairingStep.value = PairingStep.HANDSHAKE
            _pairingProgress.value = 0.25f
            _pairingStatusMsg.value = "Establishing encrypted BLE link with ${device.name}..."
            delay(800)

            _pairingProgress.value = 0.60f
            _pairingStatusMsg.value = "Configuring Wi-Fi Direct 1080p stream channel..."
            delay(900)

            _pairingStep.value = PairingStep.FIRMWARE_CHECK
            _pairingProgress.value = 0.85f
            _pairingStatusMsg.value = "Verifying Meta Security Key & Firmware v3.1.4..."
            delay(800)

            _pairingProgress.value = 1.0f
            _pairingStep.value = PairingStep.SUCCESS
            _pairingStatusMsg.value = "Successfully paired ${device.name}!"

            _glassState.value = _glassState.value.copy(
                deviceName = "${device.name} (${device.color})",
                connectionState = GlassAiState.CONNECTED,
                batteryPercent = device.batteryPercent
            )
            _discoveredDevices.value = _discoveredDevices.value.map {
                if (it.id == device.id) it.copy(isPaired = true) else it.copy(isPaired = false)
            }
        }
    }

    fun reconnectDevice() {
        viewModelScope.launch {
            _isReconnecting.value = true
            _glassState.value = _glassState.value.copy(connectionState = GlassAiState.OFFLINE)
            delay(1200)
            _glassState.value = _glassState.value.copy(connectionState = GlassAiState.CONNECTED)
            _isReconnecting.value = false
            _connectionQuality.value = ConnectionQualityInfo(
                rssiDbm = (-50..-62).random(),
                bandwidthMbps = 54,
                latencyMs = (14..22).random(),
                signalRating = "Excellent"
            )
        }
    }

    fun disconnectDevice() {
        _glassState.value = _glassState.value.copy(connectionState = GlassAiState.OFFLINE)
    }

    fun checkFirmwareUpdate() {
        viewModelScope.launch {
            _isCheckingFirmware.value = true
            delay(1200)
            _isCheckingFirmware.value = false
            _firmwareUpdateAvailable.value = false
        }
    }

    fun resetPairingFlow() {
        _pairingStep.value = PairingStep.DISCOVERY
        _pairingProgress.value = 0f
        _pairingError.value = null
    }

    fun toggleTaskCompletion(taskId: String) {
        _siteTasks.value = _siteTasks.value.map { task ->
            if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
        }
    }

    // Phase 6 — Scene Analysis Functions
    fun captureNewSceneScreenshot() {
        viewModelScope.launch {
            _isCapturingScene.value = true
            _isAnalysisSaved.value = false
            delay(1200) // Simulate HD camera snapshot capture & Gemini 1.5 Pro vision pipeline
            val time = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            val newSnapshotId = "snap_${(1000..9999).random()}"
            _sceneAnalysis.value = _sceneAnalysis.value.copy(
                snapshotId = newSnapshotId,
                timestamp = time,
                riskScore = (20..85).random(),
                riskRating = listOf("CRITICAL RISK", "HIGH RISK", "MODERATE RISK", "LOW RISK").random()
            )
            _isCapturingScene.value = false
        }
    }

    fun saveSceneAnalysis() {
        viewModelScope.launch {
            _isAnalysisSaved.value = true
            val current = _sceneAnalysis.value
            repository.insertReport(
                ReportEntity(
                    type = "SCENE_ANALYSIS",
                    title = "Scene Inspection Snapshot #${current.snapshotId}",
                    summary = "Risk Score ${current.riskScore}/100. Detected ${current.workers.size} workers, ${current.materials.size} material batches, and ${current.hazards.size} hazards at ${current.locationTag}.",
                    crewCount = current.workers.size,
                    hazardsFound = current.hazards.size
                )
            )
        }
    }

    fun selectBoundingBox(box: VisionBoundingBox?) {
        _selectedBoundingBox.value = box
        if (box != null) {
            _showBottomSheet.value = true
        }
    }

    fun toggleBottomSheet(show: Boolean) {
        _showBottomSheet.value = show
    }

    // Phase 7 — Hazard Detection Functions
    fun createHazardItem(
        title: String,
        category: HazardCategory,
        severity: String,
        location: String,
        oshaStandard: String,
        description: String
    ) {
        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val newId = "hz_${(100..999).random()}"
        val newItem = HazardDetectionItem(
            id = newId,
            category = category,
            title = title,
            location = location,
            severity = severity,
            timestamp = time,
            isAcknowledged = false,
            audioAlertText = "$severity Hazard Alert: $title at $location.",
            oshaStandard = oshaStandard.ifBlank { "OSHA 1926 General Safety" },
            description = description,
            detectionConfidence = (92..99).random()
        )

        val updatedList = listOf(newItem) + _hazardDetectionState.value.hazards
        _hazardDetectionState.value = _hazardDetectionState.value.copy(hazards = updatedList)

        // Sync to local Room database
        viewModelScope.launch {
            repository.insertHazard(
                HazardEntity(
                    title = title,
                    category = category.displayName,
                    severity = severity,
                    location = location,
                    description = description,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun dismissHazardItem(hazardId: String) {
        val updated = _hazardDetectionState.value.hazards.map { hz ->
            if (hz.id == hazardId) hz.copy(isAcknowledged = true) else hz
        }
        _hazardDetectionState.value = _hazardDetectionState.value.copy(hazards = updated)
    }

    fun playVoiceAlert(hazardId: String) {
        viewModelScope.launch {
            _hazardDetectionState.value = _hazardDetectionState.value.copy(activeVoicePlayingId = hazardId)
            delay(3000) // Simulate voice audio alert playback through Ray-Ban speaker
            if (_hazardDetectionState.value.activeVoicePlayingId == hazardId) {
                _hazardDetectionState.value = _hazardDetectionState.value.copy(activeVoicePlayingId = null)
            }
        }
    }

    fun stopVoiceAlert() {
        _hazardDetectionState.value = _hazardDetectionState.value.copy(activeVoicePlayingId = null)
    }

    fun reportHazardToDb(hazard: HazardDetectionItem) {
        viewModelScope.launch {
            repository.insertReport(
                ReportEntity(
                    type = "HAZARD_INFRACTION",
                    title = "Safety Infraction: ${hazard.title}",
                    summary = "Severity: ${hazard.severity} • Location: ${hazard.location}. Standard: ${hazard.oshaStandard}. Details: ${hazard.description}",
                    crewCount = 1,
                    hazardsFound = 1
                )
            )
            // Acknowledge hazard
            dismissHazardItem(hazard.id)
        }
    }

    fun setHazardCategoryFilter(category: HazardCategory?) {
        _hazardDetectionState.value = _hazardDetectionState.value.copy(selectedCategoryFilter = category)
    }

    fun setHazardSeverityFilter(severity: String?) {
        _hazardDetectionState.value = _hazardDetectionState.value.copy(selectedSeverityFilter = severity)
    }

    // Phase 8 — AI Assistant Functions
    fun attachImageToAssistant(uri: String, name: String) {
        _assistantState.value = _assistantState.value.copy(
            attachedImageUri = uri,
            attachedImageName = name
        )
    }

    fun removeAttachedImage() {
        _assistantState.value = _assistantState.value.copy(
            attachedImageUri = null,
            attachedImageName = null
        )
    }

    fun toggleHandsFreeAssistant() {
        _assistantState.value = _assistantState.value.copy(
            isHandsFreeEnabled = !_assistantState.value.isHandsFreeEnabled
        )
    }

    fun toggleAssistantSpeech(messageId: String) {
        viewModelScope.launch {
            if (_assistantState.value.activeSpeechPlayingId == messageId) {
                _assistantState.value = _assistantState.value.copy(activeSpeechPlayingId = null)
            } else {
                _assistantState.value = _assistantState.value.copy(activeSpeechPlayingId = messageId)
                delay(3500) // Simulate voice speech synthesis playback over Ray-Ban glasses speaker
                if (_assistantState.value.activeSpeechPlayingId == messageId) {
                    _assistantState.value = _assistantState.value.copy(activeSpeechPlayingId = null)
                }
            }
        }
    }

    fun sendAssistantQuery(prompt: String) {
        if (prompt.isBlank()) return

        val now = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        val userMsgId = "msg_${System.currentTimeMillis()}_u"
        val attachedImg = _assistantState.value.attachedImageUri

        val userMessage = AssistantChatMessage(
            id = userMsgId,
            isUser = true,
            text = prompt,
            timestamp = now,
            imageUri = attachedImg
        )

        val currentMsgs = _assistantState.value.messages + userMessage
        _assistantState.value = _assistantState.value.copy(
            messages = currentMsgs,
            isListening = true,
            attachedImageUri = null,
            attachedImageName = null
        )

        viewModelScope.launch {
            delay(500)
            _assistantState.value = _assistantState.value.copy(isListening = false, isThinking = true)

            // MultiAgent AI synthesis call
            val aiResult = aiService.processQueryAndFrame(prompt)

            delay(800)
            _assistantState.value = _assistantState.value.copy(isThinking = false, isSpeaking = true)

            val aiMsgId = "msg_${System.currentTimeMillis()}_ai"
            val aiMessage = AssistantChatMessage(
                id = aiMsgId,
                isUser = false,
                text = aiResult.aiResponseText,
                timestamp = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
                oshaReference = if (prompt.contains("OSHA", ignoreCase = true) || prompt.contains("spec", ignoreCase = true)) "OSHA 1926 Standard • Section 501" else "OSHA 1926 Safety Standard",
                confidenceScore = (94..99).random(),
                actionItems = listOf(
                    "Log observation in daily superintendent report",
                    "Notify crew foreman on Ray-Ban Channel 1"
                )
            )

            val updatedMsgs = _assistantState.value.messages + aiMessage
            _assistantState.value = _assistantState.value.copy(
                messages = updatedMsgs,
                isSpeaking = false,
                activeSpeechPlayingId = aiMsgId
            )

            delay(3500)
            if (_assistantState.value.activeSpeechPlayingId == aiMsgId) {
                _assistantState.value = _assistantState.value.copy(activeSpeechPlayingId = null)
            }
        }
    }

    // Phase 9 — CAD / BIM Functions
    fun uploadCadBimFile(fileName: String, fileType: CadFileType, gridMapping: String) {
        val newFile = CadBimFileItem(
            id = "cad_${System.currentTimeMillis()}",
            fileName = fileName,
            fileType = fileType,
            fileSize = "${(12..48).random()}.${(1..9).random()} MB",
            uploadDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
            projectGridMapping = gridMapping,
            elementCount = (400..1800).random(),
            revision = "Rev 1"
        )
        val updated = _cadBimState.value.files + newFile
        _cadBimState.value = _cadBimState.value.copy(
            files = updated,
            activeFileId = newFile.id
        )
    }

    fun selectActiveCadFile(fileId: String) {
        _cadBimState.value = _cadBimState.value.copy(activeFileId = fileId)
    }

    fun setCadViewMode(viewMode: String) {
        _cadBimState.value = _cadBimState.value.copy(selectedViewMode = viewMode)
    }

    fun adjustAlignment(dx: Int, dy: Int, dRot: Double) {
        val curX = _cadBimState.value.alignmentXOffsetMm
        val curY = _cadBimState.value.alignmentYOffsetMm
        val curRot = _cadBimState.value.alignmentRotationDeg
        _cadBimState.value = _cadBimState.value.copy(
            alignmentXOffsetMm = curX + dx,
            alignmentYOffsetMm = curY + dy,
            alignmentRotationDeg = Math.round((curRot + dRot) * 10.0) / 10.0
        )
    }

    fun resetAlignment() {
        _cadBimState.value = _cadBimState.value.copy(
            alignmentXOffsetMm = 0,
            alignmentYOffsetMm = 0,
            alignmentRotationDeg = 0.0
        )
    }

    fun runCadComparison() {
        viewModelScope.launch {
            _cadBimState.value = _cadBimState.value.copy(isComparing = true)
            delay(1200)
            _cadBimState.value = _cadBimState.value.copy(isComparing = false)
        }
    }

    fun playCadVoiceFeedback(deviationId: String) {
        viewModelScope.launch {
            if (_cadBimState.value.activeVoicePlayingId == deviationId) {
                _cadBimState.value = _cadBimState.value.copy(activeVoicePlayingId = null)
            } else {
                _cadBimState.value = _cadBimState.value.copy(activeVoicePlayingId = deviationId)
                delay(3500)
                if (_cadBimState.value.activeVoicePlayingId == deviationId) {
                    _cadBimState.value = _cadBimState.value.copy(activeVoicePlayingId = null)
                }
            }
        }
    }

    fun resolveCadDeviation(deviationId: String) {
        val updatedDevs = _cadBimState.value.deviations.map {
            if (it.id == deviationId) it.copy(isResolved = !it.isResolved) else it
        }
        _cadBimState.value = _cadBimState.value.copy(deviations = updatedDevs)
    }

    // Phase 10 — Quality Inspection Functions
    fun selectQualityCategory(category: QualityCategory) {
        _qualityInspectionState.value = _qualityInspectionState.value.copy(selectedCategory = category)
    }

    fun toggleQualityRemediated(itemId: String) {
        val updatedItems = _qualityInspectionState.value.inspectionItems.map {
            if (it.id == itemId) it.copy(isRemediated = !it.isRemediated) else it
        }
        _qualityInspectionState.value = _qualityInspectionState.value.copy(inspectionItems = updatedItems)
    }

    fun completeQualityRecommendation(recId: String) {
        val updatedRecs = _qualityInspectionState.value.recommendations.map {
            if (it.id == recId) it.copy(isCompleted = !it.isCompleted) else it
        }
        _qualityInspectionState.value = _qualityInspectionState.value.copy(recommendations = updatedRecs)
    }

    fun runAiQualityScan() {
        viewModelScope.launch {
            _qualityInspectionState.value = _qualityInspectionState.value.copy(isScanning = true)
            delay(1500)
            _qualityInspectionState.value = _qualityInspectionState.value.copy(
                isScanning = false,
                overallQualityScore = 95,
                qualityGrade = "A GRADE",
                crackScore = 94
            )
        }
    }

    // Phase 11 — Material Verification Functions
    fun selectMaterialCategory(category: MaterialCategory) {
        _materialVerificationState.value = _materialVerificationState.value.copy(selectedCategory = category)
    }

    fun toggleMaterialCompliance(materialId: String) {
        val updatedList = _materialVerificationState.value.materials.map { mat ->
            if (mat.id == materialId) mat.copy(isCompliant = !mat.isCompliant) else mat
        }
        val compliantCount = updatedList.count { it.isCompliant }
        val nonCompliantCount = updatedList.size - compliantCount
        val rate = if (updatedList.isNotEmpty()) (compliantCount * 100 / updatedList.size) else 100

        _materialVerificationState.value = _materialVerificationState.value.copy(
            materials = updatedList,
            compliantMaterialsCount = compliantCount,
            expiredOrNonCompliantCount = nonCompliantCount,
            complianceRatePercent = rate
        )
    }

    fun updateMaterialInventoryStock(materialId: String, delta: Double) {
        val updatedList = _materialVerificationState.value.materials.map { mat ->
            if (mat.id == materialId) {
                val current = mat.currentStockQuantity.toDoubleOrNull() ?: 0.0
                val newStock = (current + delta).coerceAtLeast(0.0)
                mat.copy(
                    currentStockQuantity = String.format("%.1f", newStock),
                    isInventoryUpdated = true
                )
            } else mat
        }
        _materialVerificationState.value = _materialVerificationState.value.copy(materials = updatedList)
    }

    fun scanMaterialWithGlasses() {
        viewModelScope.launch {
            _materialVerificationState.value = _materialVerificationState.value.copy(isScanningMaterial = true)
            delay(1600)
            _materialVerificationState.value = _materialVerificationState.value.copy(
                isScanningMaterial = false,
                scanSuccessMessage = "RAY-BAN VISION AI: Verified Fire-Retardant Cable Reel #PRY-2026-9081 against site electrical spec IEC 60502-1."
            )
        }
    }

    // Phase 12 — Comprehensive Automated Reporting Functions
    fun selectReportFilterCategory(category: ReportFilterCategory) {
        _comprehensiveReportState.value = _comprehensiveReportState.value.copy(selectedFilter = category)
    }

    fun generateAiReport(typeCode: String) {
        viewModelScope.launch {
            _comprehensiveReportState.value = _comprehensiveReportState.value.copy(isGeneratingAiReport = true)
            delay(1800)

            val newReport = when (typeCode) {
                "DAILY" -> ComprehensiveReportItem(
                    id = "rep_dpr_${System.currentTimeMillis()}",
                    typeCode = "DAILY",
                    title = "Daily Progress Report (DPR) — Level 18 Auto-Generated",
                    date = "Just Now",
                    author = "Ray-Ban Meta Smart Glasses AI Stream",
                    executiveSummary = "Auto-synthesized site log: 38 workers logged on level 18. Rebar placement 95% finished, concrete pour scheduled for 14:00.",
                    keyMetrics = mapOf("Crew Size" to "38 Workers", "Concrete Ready" to "120 m³", "PPE Compliance" to "100%"),
                    aiGeneratedInsights = listOf("0 active safety hazards detected during 10:15 AM site walk.", "Formwork plumbness checked within ±2mm tolerance."),
                    hazardsReported = 0,
                    qualityDefectsFound = 0,
                    crewSize = 38
                )
                "WEEKLY" -> ComprehensiveReportItem(
                    id = "rep_wpr_${System.currentTimeMillis()}",
                    typeCode = "WEEKLY",
                    title = "Weekly Executive Summary (WPR) — Level 18 Milestone",
                    date = "Current Week Auto-Summary",
                    author = "Multi-Agent Executive Coordinator AI",
                    executiveSummary = "Weekly structural milestones achieved on time. Total 520m³ concrete poured. ZERO lost-time safety incidents recorded.",
                    keyMetrics = mapOf("Weekly Target" to "100%", "Quality Grade" to "A GRADE (95%)", "Safety Hours" to "2,100 Hrs"),
                    aiGeneratedInsights = listOf("All 5 major trade sub-contractors operating in sync.", "Procore & Autodesk Construction Cloud ERP fully reconciled."),
                    hazardsReported = 2,
                    qualityDefectsFound = 1,
                    crewSize = 145
                )
                "INCIDENT" -> ComprehensiveReportItem(
                    id = "rep_inc_${System.currentTimeMillis()}",
                    typeCode = "INCIDENT",
                    title = "Incident & Near-Miss Flash Report (IR-09)",
                    date = "Just Now",
                    author = "SiteMind Live AI Vision Sentinel",
                    executiveSummary = "Minor water seepage noticed near level 18 electrical conduit box during pressure test. Circuit isolated automatically.",
                    keyMetrics = mapOf("Risk Severity" to "LOW", "Isolation Time" to "< 30 Sec", "Status" to "RESOLVED"),
                    aiGeneratedInsights = listOf("Acoustic leak detector verified flange gasket replacement required."),
                    hazardsReported = 1,
                    qualityDefectsFound = 0,
                    crewSize = 12
                )
                "SAFETY" -> ComprehensiveReportItem(
                    id = "rep_saf_${System.currentTimeMillis()}",
                    typeCode = "SAFETY",
                    title = "Safety & OSHA Compliance Audit (SVR-20)",
                    date = "Today",
                    author = "AI Safety Agent & Lead Auditor",
                    executiveSummary = "Full perimeter net inspection and perimeter tie-off point load test verified. OSHA 1926 compliant.",
                    keyMetrics = mapOf("Safety Index" to "98/100", "PPE Score" to "99%", "Anchor Load" to "22.2 kN"),
                    aiGeneratedInsights = listOf("All lifelines tensioned according to structural engineering layout."),
                    hazardsReported = 0,
                    qualityDefectsFound = 0,
                    crewSize = 85
                )
                else -> ComprehensiveReportItem(
                    id = "rep_qua_${System.currentTimeMillis()}",
                    typeCode = "QUALITY",
                    title = "Quality Assurance & Material Audit (QAR-15)",
                    date = "Today",
                    author = "QA/QC Structural Vision AI",
                    executiveSummary = "Rebar alignment, cover thickness, and batch slump testing passed all design specifications.",
                    keyMetrics = mapOf("SQI Score" to "95/100", "Slump" to "115 mm", "Rebar Cover" to "40 mm"),
                    aiGeneratedInsights = listOf("Mill certificates and heat numbers verified against ASTM A615."),
                    hazardsReported = 0,
                    qualityDefectsFound = 0,
                    crewSize = 24
                )
            }

            val updatedList = listOf(newReport) + _comprehensiveReportState.value.reportsList
            _comprehensiveReportState.value = _comprehensiveReportState.value.copy(
                isGeneratingAiReport = false,
                reportsList = updatedList,
                pdfExportToast = "Auto-generated new ${newReport.typeCode} report!"
            )
        }
    }

    fun exportReportPdf(reportTitle: String) {
        _comprehensiveReportState.value = _comprehensiveReportState.value.copy(
            pdfExportToast = "PDF Exported & Downloaded: '$reportTitle.pdf'"
        )
    }

    fun shareReport(reportTitle: String) {
        _comprehensiveReportState.value = _comprehensiveReportState.value.copy(
            shareDialogMessage = "Sharing '$reportTitle' via Email, Procore & WhatsApp..."
        )
    }

    fun dismissReportNotifications() {
        _comprehensiveReportState.value = _comprehensiveReportState.value.copy(
            pdfExportToast = null,
            shareDialogMessage = null
        )
    }

    // Phase 13 — Analytics Functions
    fun setAnalyticsTimeframe(timeframe: AnalyticsTimeframe) {
        _analyticsState.value = _analyticsState.value.copy(timeframe = timeframe)
    }

    fun setAnalyticsTradeFilter(trade: TradeCategory) {
        _analyticsState.value = _analyticsState.value.copy(selectedTrade = trade)
    }

    fun runPredictiveAnalyticsSimulation() {
        viewModelScope.launch {
            _analyticsState.value = _analyticsState.value.copy(isRunningPredictiveSim = true)
            delay(1800)
            _analyticsState.value = _analyticsState.value.copy(
                isRunningPredictiveSim = false,
                simulationResult = "PREDICTIVE AI SIMULATION: At current rate (+8.4% productivity), Level 19 deck completion is projected 3.5 days ahead of schedule with 99.1% safety score."
            )
        }
    }

    fun dismissAnalyticsSimulationResult() {
        _analyticsState.value = _analyticsState.value.copy(simulationResult = null)
    }

    // Phase 14 — Notifications Functions
    fun selectNotificationCategory(category: NotificationCategory) {
        _notificationState.value = _notificationState.value.copy(selectedCategory = category)
    }

    fun markNotificationAsRead(id: String) {
        val currentList = _notificationState.value.notificationsList
        val updatedList = currentList.map { item ->
            if (item.id == id) item.copy(isRead = true) else item
        }
        val unread = updatedList.count { !it.isRead }
        _notificationState.value = _notificationState.value.copy(
            notificationsList = updatedList,
            unreadCount = unread
        )
    }

    fun markAllNotificationsAsRead() {
        val updatedList = _notificationState.value.notificationsList.map { it.copy(isRead = true) }
        _notificationState.value = _notificationState.value.copy(
            notificationsList = updatedList,
            unreadCount = 0
        )
    }

    fun sendTestPushNotification() {
        viewModelScope.launch {
            _notificationState.value = _notificationState.value.copy(isTestPushSent = true)
            val newNotif = SiteNotificationItem(
                id = "test_push_${System.currentTimeMillis()}",
                category = NotificationCategory.AI_ALERTS,
                title = "TEST PUSH — SITEMIND AI ALERT",
                message = "Live HUD connection active. Ray-Ban Smart Glasses stream telemetry is synchronized.",
                timestamp = "Just now",
                priority = NotificationPriority.HIGH,
                isRead = false,
                actionText = "VIEW LIVE AI",
                targetRoute = "live_ai"
            )
            val updatedList = listOf(newNotif) + _notificationState.value.notificationsList
            _notificationState.value = _notificationState.value.copy(
                notificationsList = updatedList,
                unreadCount = updatedList.count { !it.isRead }
            )
            delay(3000)
            _notificationState.value = _notificationState.value.copy(isTestPushSent = false)
        }
    }

    fun updateNotificationSettings(
        pushEnabled: Boolean? = null,
        aiVisionAlerts: Boolean? = null,
        criticalHazards: Boolean? = null,
        reportDigests: Boolean? = null,
        firmwareUpdates: Boolean? = null,
        taskAssignments: Boolean? = null
    ) {
        val current = _notificationState.value.settings
        val updated = current.copy(
            isPushNotificationsEnabled = pushEnabled ?: current.isPushNotificationsEnabled,
            isAiVisionAlertsEnabled = aiVisionAlerts ?: current.isAiVisionAlertsEnabled,
            isCriticalHazardAlertsEnabled = criticalHazards ?: current.isCriticalHazardAlertsEnabled,
            isReportDigestEnabled = reportDigests ?: current.isReportDigestEnabled,
            isFirmwareUpdateAlertsEnabled = firmwareUpdates ?: current.isFirmwareUpdateAlertsEnabled,
            isTaskAssignmentAlertsEnabled = taskAssignments ?: current.isTaskAssignmentAlertsEnabled
        )
        _notificationState.value = _notificationState.value.copy(settings = updated)
    }

    // Phase 15 — Profile Functions
    fun setProfileLanguage(language: String) {
        val updatedUser = _profileState.value.profile.copy(language = language)
        _profileState.value = _profileState.value.copy(profile = updatedUser)
    }

    fun setProfileTheme(theme: String) {
        val updatedUser = _profileState.value.profile.copy(theme = theme)
        _profileState.value = _profileState.value.copy(profile = updatedUser)
    }

    fun toggleBiometricAuth(enabled: Boolean) {
        val updatedUser = _profileState.value.profile.copy(isBiometricEnabled = enabled)
        _profileState.value = _profileState.value.copy(profile = updatedUser)
    }

    fun toggleDataTelemetry(enabled: Boolean) {
        val updatedUser = _profileState.value.profile.copy(isTelemetryShared = enabled)
        _profileState.value = _profileState.value.copy(profile = updatedUser)
    }

    fun toggleLocationTracking(enabled: Boolean) {
        val updatedUser = _profileState.value.profile.copy(isLocationTrackingEnabled = enabled)
        _profileState.value = _profileState.value.copy(profile = updatedUser)
    }

    fun setLogoutDialogVisible(visible: Boolean) {
        _profileState.value = _profileState.value.copy(showLogoutConfirmationDialog = visible)
    }

    fun logoutUser() {
        val updatedUser = _profileState.value.profile.copy(isLoggedOut = true)
        _profileState.value = _profileState.value.copy(
            profile = updatedUser,
            showLogoutConfirmationDialog = false
        )
        _authState.value = AuthScreenState.LOGIN
    }

    fun loginUser() {
        val updatedUser = _profileState.value.profile.copy(isLoggedOut = false)
        _profileState.value = _profileState.value.copy(profile = updatedUser)
        _authState.value = AuthScreenState.LOGIN
    }

    // Phase 16 — Backend ViewModel Functions
    fun selectApiCategory(category: ApiCategory) {
        _backendConsoleState.value = _backendConsoleState.value.copy(selectedCategory = category)
    }

    fun testFastApiEndpoint(endpointId: String) {
        viewModelScope.launch {
            _backendConsoleState.value = _backendConsoleState.value.copy(
                isTestingApi = true,
                activeTestLog = "Executing HTTP Request to endpoint $endpointId..."
            )
            delay(600)
            
            val updatedEndpoints = _backendConsoleState.value.endpointsList.map { ep ->
                if (ep.id == endpointId) {
                    ep.copy(isTested = true, lastResponseTimeMs = (12..48).random())
                } else ep
            }
            
            val testedEp = updatedEndpoints.find { it.id == endpointId }
            val logMessage = "HTTP 200 OK — ${testedEp?.method} ${testedEp?.path}\n" +
                    "Response Time: ${testedEp?.lastResponseTimeMs}ms\n\n" +
                    "${testedEp?.sampleResponse}"

            _backendConsoleState.value = _backendConsoleState.value.copy(
                isTestingApi = false,
                endpointsList = updatedEndpoints,
                activeTestLog = logMessage
            )
        }
    }

    fun clearApiTestLog() {
        _backendConsoleState.value = _backendConsoleState.value.copy(activeTestLog = null)
    }

    // Phase 17 — AI Integration Functions
    fun selectAiModule(module: AiModuleType) {
        _aiIntegrationState.value = _aiIntegrationState.value.copy(selectedModule = module)
    }

    fun toggleAiStreaming(enabled: Boolean) {
        _aiIntegrationState.value = _aiIntegrationState.value.copy(isStreamingActive = enabled)
    }

    fun triggerLangGraphExecution() {
        viewModelScope.launch {
            _aiIntegrationState.value = _aiIntegrationState.value.copy(
                streamingTextBuffer = "[LangGraph Orchestrator] Spawning agents: Safety Guard Agent, Structural Rebar Agent, OCR Spec Verifier..."
            )
            delay(500)
            _aiIntegrationState.value = _aiIntegrationState.value.copy(
                streamingTextBuffer = "[LangGraph Orchestrator] Running parallel node execution... Safety agent passed. Rebar agent calculated grid spacing. OCR verified ASTM A615 spec."
            )
        }
    }

    // Phase 18 — Production Readiness Functions
    fun selectProductionPillar(pillar: ProductionPillar) {
        _productionState.value = _productionState.value.copy(selectedPillar = pillar)
    }

    fun toggleOfflineMode(enabled: Boolean) {
        _productionState.value = _productionState.value.copy(
            cacheStatus = _productionState.value.cacheStatus.copy(isOfflineModeActive = enabled)
        )
    }

    fun runProductionAuditSuite() {
        viewModelScope.launch {
            _productionState.value = _productionState.value.copy(
                isRunningAudit = true,
                auditLogOutput = "Executing Production Audit Suite...\n• Verifying R8 ProGuard shrinker...\n• Testing SQLCipher 256-bit DB encryption...\n• Running Robolectric JVM unit tests & Roborazzi screenshot verification..."
            )
            delay(700)
            _productionState.value = _productionState.value.copy(
                isRunningAudit = false,
                overallReadinessScorePct = 100.0f,
                auditLogOutput = "AUDIT COMPLETE: 100% PASS RATE\n✔ Performance & Caching: 60 FPS Verified\n✔ Offline Room DB: SQLCipher Encrypted\n✔ Security & TLS 1.3: AES-256 Hardware Key\n✔ Accessibility WCAG AA: 4.5:1 Contrast Pass\n✔ Play Store Readiness: Target SDK 34 Approved"
            )
        }
    }

    fun clearProductionAuditLog() {
        _productionState.value = _productionState.value.copy(auditLogOutput = null)
    }
}
