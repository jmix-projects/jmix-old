/*
 * Copyright 2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.ui.icon;

/**
 * Default icon set. It includes almost full FontAwesome icon set and CUBA-specific icons.
 */
public enum JmixIcon implements Icons.Icon {
    OK("font-icon:CHECK"),
    CANCEL("font-icon:BAN"),
    YES("font-icon:CHECK"),
    NO("font-icon:BAN"),
    CLOSE("font-icon:CLOSE"),

    DIALOG_OK("font-icon:CHECK"),
    DIALOG_CANCEL("font-icon:BAN"),
    DIALOG_YES("font-icon:CHECK"),
    DIALOG_NO("font-icon:BAN"),
    DIALOG_CLOSE(""),

    CREATE_ACTION("font-icon:FILE_O"),
    EDIT_ACTION("font-icon:PENCIL"),
    VIEW_ACTION("font-icon:EYE"),
    REMOVE_ACTION("font-icon:TIMES"),
    REFRESH_ACTION("font-icon:REFRESH"),
    ADD_ACTION("font-icon:FILE_O"),
    EXCLUDE_ACTION("font-icon:TIMES"),
    EXCEL_ACTION("font-icon:FILE_EXCEL_O"),
    UNLOCK_ACTION("font-icon:UNLOCK"),
    BULK_EDIT_ACTION("font-icon:TABLE"),

    ENTITYPICKER_CLEAR("font-icon:TIMES"),
    ENTITYPICKER_CLEAR_READONLY("font-icon:TIMES"),
    ENTITYPICKER_LOOKUP("font-icon:ELLIPSIS_H"),
    ENTITYPICKER_LOOKUP_READONLY("font-icon:ELLIPSIS_H"),
    ENTITYPICKER_OPEN("font-icon:SEARCH"),
    ENTITYPICKER_OPEN_READONLY("font-icon:SEARCH"),

    LOOKUP_OK("font-icon:CHECK"),
    LOOKUP_CANCEL("font-icon:BAN"),
    EDITOR_OK("font-icon:CHECK"),
    EDITOR_CANCEL("font-icon:BAN"),

    ENABLE_EDITING("font-icon:PENCIL"),

    _500PX("font-icon:_500PX"),
    ADDRESS_BOOK("font-icon:ADDRESS_BOOK"),
    ADDRESS_BOOK_O("font-icon:ADDRESS_BOOK_O"),
    ADDRESS_CARD("font-icon:ADDRESS_CARD"),
    ADDRESS_CARD_O("font-icon:ADDRESS_CARD_O"),
    ADJUST("font-icon:ADJUST"),
    ADN("font-icon:ADN"),
    ALIGN_CENTER("font-icon:ALIGN_CENTER"),
    ALIGN_JUSTIFY("font-icon:ALIGN_JUSTIFY"),
    ALIGN_LEFT("font-icon:ALIGN_LEFT"),
    ALIGN_RIGHT("font-icon:ALIGN_RIGHT"),
    AMAZON("font-icon:AMAZON"),
    AMBULANCE("font-icon:AMBULANCE"),
    AMERICAN_SIGN_LANGUAGE_INTERPRETING("font-icon:AMERICAN_SIGN_LANGUAGE_INTERPRETING"),
    ANCHOR("font-icon:ANCHOR"),
    ANDROID("font-icon:ANDROID"),
    ANGELLIST("font-icon:ANGELLIST"),
    ANGLE_DOUBLE_DOWN("font-icon:ANGLE_DOUBLE_DOWN"),
    ANGLE_DOUBLE_LEFT("font-icon:ANGLE_DOUBLE_LEFT"),
    ANGLE_DOUBLE_RIGHT("font-icon:ANGLE_DOUBLE_RIGHT"),
    ANGLE_DOUBLE_UP("font-icon:ANGLE_DOUBLE_UP"),
    ANGLE_DOWN("font-icon:ANGLE_DOWN"),
    ANGLE_LEFT("font-icon:ANGLE_LEFT"),
    ANGLE_RIGHT("font-icon:ANGLE_RIGHT"),
    ANGLE_UP("font-icon:ANGLE_UP"),
    APPLE("font-icon:APPLE"), //
    ARCHIVE("font-icon:ARCHIVE"),
    AREA_CHART("font-icon:AREA_CHART"),
    ARROW_CIRCLE_DOWN("font-icon:ARROW_CIRCLE_DOWN"),
    ARROW_CIRCLE_LEFT("font-icon:ARROW_CIRCLE_LEFT"),
    ARROW_CIRCLE_O_DOWN("font-icon:ARROW_CIRCLE_O_DOWN"),
    ARROW_CIRCLE_O_LEFT("font-icon:ARROW_CIRCLE_O_LEFT"),
    ARROW_CIRCLE_O_RIGHT("font-icon:ARROW_CIRCLE_O_RIGHT"),
    ARROW_CIRCLE_O_UP("font-icon:ARROW_CIRCLE_O_UP"),
    ARROW_CIRCLE_RIGHT("font-icon:ARROW_CIRCLE_RIGHT"),
    ARROW_CIRCLE_UP("font-icon:ARROW_CIRCLE_UP"),
    ARROW_DOWN("font-icon:ARROW_DOWN"),
    ARROW_LEFT("font-icon:ARROW_LEFT"),
    ARROW_RIGHT("font-icon:ARROW_RIGHT"),
    ARROW_UP("font-icon:ARROW_UP"),
    ARROWS("font-icon:ARROWS"),
    ARROWS_ALT("font-icon:ARROWS_ALT"),
    ARROWS_H("font-icon:ARROWS_H"),
    ARROWS_V("font-icon:ARROWS_V"),
    ASL_INTERPRETING("font-icon:ASL_INTERPRETING"),
    ASSISTIVE_LISTENING_SYSTEMS("font-icon:ASSISTIVE_LISTENING_SYSTEMS"),
    ASTERISK("font-icon:ASTERISK"),
    AT("font-icon:AT"),
    AUDIO_DESCRIPTION("font-icon:AUDIO_DESCRIPTION"),
    AUTOMOBILE("font-icon:AUTOMOBILE"),
    BACKWARD("font-icon:BACKWARD"),
    BALANCE_SCALE("font-icon:BALANCE_SCALE"),
    BAN("font-icon:BAN"),
    BANDCAMP("font-icon:BANDCAMP"),
    BANK("font-icon:BANK"),
    BAR_CHART("font-icon:BAR_CHART"),
    BAR_CHART_O("font-icon:BAR_CHART_O"),
    BARCODE("font-icon:BARCODE"),
    BARS("font-icon:BARS"),
    BATH("font-icon:BATH"),
    BATHTUB("font-icon:BATHTUB"),
    BATTERY_0("font-icon:BATTERY_0"),
    BATTERY_1("font-icon:BATTERY_1"),
    BATTERY_2("font-icon:BATTERY_2"),
    BATTERY_3("font-icon:BATTERY_3"),
    BATTERY_4("font-icon:BATTERY_4"),
    BATTERY_EMPTY("font-icon:BATTERY_EMPTY"),
    BATTERY_FULL("font-icon:BATTERY_FULL"),
    BATTERY_HALF("font-icon:BATTERY_HALF"),
    BATTERY_QUARTER("font-icon:BATTERY_QUARTER"),
    BATTERY_THREE_QUARTERS("font-icon:BATTERY_THREE_QUARTERS"),
    BED("font-icon:BED"),
    BEER("font-icon:BEER"),
    BEHANCE("font-icon:BEHANCE"),
    BEHANCE_SQUARE("font-icon:BEHANCE_SQUARE"),
    BELL("font-icon:BELL"),
    BELL_O("font-icon:BELL_O"),
    BELL_SLASH("font-icon:BELL_SLASH"),
    BELL_SLASH_O("font-icon:BELL_SLASH_O"),
    BICYCLE("font-icon:BICYCLE"),
    BINOCULARS("font-icon:BINOCULARS"),
    BIRTHDAY_CAKE("font-icon:BIRTHDAY_CAKE"),
    BITBUCKET("font-icon:BITBUCKET"),
    BITBUCKET_SQUARE("font-icon:BITBUCKET_SQUARE"),
    BITCOIN("font-icon:BITCOIN"),
    BLIND("font-icon:BLIND"),
    BLACK_TIE("font-icon:BLACK_TIE"),
    BLUETOOTH("font-icon:BLUETOOTH"),
    BLUETOOTH_B("font-icon:BLUETOOTH_B"),
    BOLD("font-icon:BOLD"),
    BOLT("font-icon:BOLT"),
    BOMB("font-icon:BOMB"),
    BOOK("font-icon:BOOK"),
    BOOKMARK("font-icon:BOOKMARK"),
    BOOKMARK_O("font-icon:BOOKMARK_O"),
    BRAILLE("font-icon:BRAILLE"),
    BRIEFCASE("font-icon:BRIEFCASE"),
    BTC("font-icon:BTC"),
    BUG("font-icon:BUG"),
    BUILDING("font-icon:BUILDING"),
    BUILDING_O("font-icon:BUILDING_O"),
    BULLHORN("font-icon:BULLHORN"),
    BULLSEYE("font-icon:BULLSEYE"),
    BUS("font-icon:BUS"),
    BUYSELLADS("font-icon:BUYSELLADS"),
    CAB("font-icon:CAB"),
    CALCULATOR("font-icon:CALCULATOR"),
    CALENDAR("font-icon:CALENDAR"),
    CALENDAR_CHECK_O("font-icon:CALENDAR_CHECK_O"),
    CALENDAR_MINUS_O("font-icon:CALENDAR_MINUS_O"),
    CALENDAR_O("font-icon:CALENDAR_O"),
    CALENDAR_PLUS_O("font-icon:CALENDAR_PLUS_O"),
    CALENDAR_TIMES_O("font-icon:CALENDAR_TIMES_O"),
    CAMERA("font-icon:CAMERA"),
    CAMERA_RETRO("font-icon:CAMERA_RETRO"),
    CAR("font-icon:CAR"),
    CARET_DOWN("font-icon:CARET_DOWN"),
    CARET_LEFT("font-icon:CARET_LEFT"),
    CARET_RIGHT("font-icon:CARET_RIGHT"),
    CARET_SQUARE_O_DOWN("font-icon:CARET_SQUARE_O_DOWN"),
    CARET_SQUARE_O_LEFT("font-icon:CARET_SQUARE_O_LEFT"),
    CARET_SQUARE_O_RIGHT("font-icon:CARET_SQUARE_O_RIGHT"),
    CARET_SQUARE_O_UP("font-icon:CARET_SQUARE_O_UP"),
    CARET_UP("font-icon:CARET_UP"),
    CART_ARROW_DOWN("font-icon:CART_ARROW_DOWN"),
    CART_PLUS("font-icon:CART_PLUS"),
    CC("font-icon:CC"),
    CC_AMEX("font-icon:CC_AMEX"),
    CC_DINERS_CLUB("font-icon:CC_DINERS_CLUB"),
    CC_DISCOVER("font-icon:CC_DISCOVER"),
    CC_JCB("font-icon:CC_JCB"),
    CC_MASTERCARD("font-icon:CC_MASTERCARD"),
    CC_PAYPAL("font-icon:CC_PAYPAL"),
    CC_STRIPE("font-icon:CC_STRIPE"),
    CC_VISA("font-icon:CC_VISA"),
    CERTIFICATE("font-icon:CERTIFICATE"),
    CHAIN("font-icon:CHAIN"),
    CHAIN_BROKEN("font-icon:CHAIN_BROKEN"),
    CHECK("font-icon:CHECK"),
    CHECK_CIRCLE("font-icon:CHECK_CIRCLE"),
    CHECK_CIRCLE_O("font-icon:CHECK_CIRCLE_O"),
    CHECK_SQUARE("font-icon:CHECK_SQUARE"),
    CHECK_SQUARE_O("font-icon:CHECK_SQUARE_O"),
    CHEVRON_CIRCLE_DOWN("font-icon:CHEVRON_CIRCLE_DOWN"),
    CHEVRON_CIRCLE_LEFT("font-icon:CHEVRON_CIRCLE_LEFT"),
    CHEVRON_CIRCLE_RIGHT("font-icon:CHEVRON_CIRCLE_RIGHT"),
    CHEVRON_CIRCLE_UP("font-icon:CHEVRON_CIRCLE_UP"),
    CHEVRON_DOWN("font-icon:CHEVRON_DOWN"),
    CHEVRON_LEFT("font-icon:CHEVRON_LEFT"),
    CHEVRON_RIGHT("font-icon:CHEVRON_RIGHT"),
    CHEVRON_UP("font-icon:CHEVRON_UP"),
    CHILD("font-icon:CHILD"),
    CHROME("font-icon:CHROME"),
    CIRCLE("font-icon:CIRCLE"),
    CIRCLE_O("font-icon:CIRCLE_O"),
    CIRCLE_O_NOTCH("font-icon:CIRCLE_O_NOTCH"),
    CIRCLE_THIN("font-icon:CIRCLE_THIN"),
    CLIPBOARD("font-icon:CLIPBOARD"),
    CLOCK_O("font-icon:CLOCK_O"),
    CLONE("font-icon:CLONE"),
    CLOUD("font-icon:CLOUD"),
    CLOUD_DOWNLOAD("font-icon:CLOUD_DOWNLOAD"),
    CLOUD_UPLOAD("font-icon:CLOUD_UPLOAD"),
    CNY("font-icon:CNY"),
    CODE("font-icon:CODE"),
    CODE_FORK("font-icon:CODE_FORK"),
    CODEPEN("font-icon:CODEPEN"),
    CODIEPIE("font-icon:CODIEPIE"),
    COFFEE("font-icon:COFFEE"),
    COG("font-icon:COG"),
    COGS("font-icon:COGS"),
    COLUMNS("font-icon:COLUMNS"),
    COMMENT("font-icon:COMMENT"),
    COMMENT_O("font-icon:COMMENT_O"),
    COMMENTING("font-icon:COMMENTING"),
    COMMENTING_O("font-icon:COMMENTING_O"),
    COMMENTS("font-icon:COMMENTS"),
    COMMENTS_O("font-icon:COMMENTS_O"),
    COMPASS("font-icon:COMPASS"),
    COMPRESS("font-icon:COMPRESS"),
    CONNECTDEVELOP("font-icon:CONNECTDEVELOP"),
    CONTAO("font-icon:CONTAO"),
    COPY("font-icon:COPY"),
    COPYRIGHT("font-icon:COPYRIGHT"),
    CREATIVE_COMMONS("font-icon:CREATIVE_COMMONS"),
    CREDIT_CARD("font-icon:CREDIT_CARD"),
    CREDIT_CARD_ALT("font-icon:CREDIT_CARD_ALT"),
    CROP("font-icon:CROP"),
    CROSSHAIRS("font-icon:CROSSHAIRS"),
    CSS3("font-icon:CSS3"),
    CUBE("font-icon:CUBE"),
    CUBES("font-icon:CUBES"),
    CUT("font-icon:CUT"),
    CUTLERY("font-icon:CUTLERY"),
    DASHBOARD("font-icon:DASHBOARD"),
    DASHCUBE("font-icon:DASHCUBE"),
    DATABASE("font-icon:DATABASE"),
    DEAF("font-icon:DEAF"),
    DEAFNESS("font-icon:DEAFNESS"),
    DEDENT("font-icon:DEDENT"),
    DELICIOUS("font-icon:DELICIOUS"),
    DESKTOP("font-icon:DESKTOP"),
    DEVIANTART("font-icon:DEVIANTART"),
    DIAMOND("font-icon:DIAMOND"),
    DIGG("font-icon:DIGG"),
    DOLLAR("font-icon:DOLLAR"),
    DOT_CIRCLE_O("font-icon:DOT_CIRCLE_O"),
    DOWNLOAD("font-icon:DOWNLOAD"),
    DRIBBBLE("font-icon:DRIBBBLE"),
    DRIVERS_LICENSE("font-icon:DRIVERS_LICENSE"),
    DRIVERS_LICENSE_O("font-icon:DRIVERS_LICENSE_O"),
    DROPBOX("font-icon:DROPBOX"),
    DRUPAL("font-icon:DRUPAL"),
    EDGE("font-icon:EDGE"),
    EDIT("font-icon:EDIT"),
    EJECT("font-icon:EJECT"),
    EERCAST("font-icon:EERCAST"),
    ELLIPSIS_H("font-icon:ELLIPSIS_H"),
    ELLIPSIS_V("font-icon:ELLIPSIS_V"),
    EMPIRE("font-icon:EMPIRE"),
    ENVELOPE("font-icon:ENVELOPE"),
    ENVELOPE_O("font-icon:ENVELOPE_O"),
    ENVELOPE_OPEN("font-icon:ENVELOPE_OPEN"),
    ENVELOPE_OPEN_O("font-icon:ENVELOPE_OPEN_O"),
    ENVELOPE_SQUARE("font-icon:ENVELOPE_SQUARE"),
    ENVIRA("font-icon:ENVIRA"),
    ERASER("font-icon:ERASER"),
    ETSY("font-icon:ETSY"),
    EUR("font-icon:EUR"),
    EURO("font-icon:EURO"),
    EXCHANGE("font-icon:EXCHANGE"),
    EXCLAMATION("font-icon:EXCLAMATION"),
    EXCLAMATION_CIRCLE("font-icon:EXCLAMATION_CIRCLE"),
    EXCLAMATION_TRIANGLE("font-icon:EXCLAMATION_TRIANGLE"),
    EXPAND("font-icon:EXPAND"),
    EXPEDITEDSSL("font-icon:EXPEDITEDSSL"),
    EXTERNAL_LINK("font-icon:EXTERNAL_LINK"),
    EXTERNAL_LINK_SQUARE("font-icon:EXTERNAL_LINK_SQUARE"),
    EYE("font-icon:EYE"),
    EYE_SLASH("font-icon:EYE_SLASH"),
    EYEDROPPER("font-icon:EYEDROPPER"),
    FA("font-icon:FA"),
    FACEBOOK("font-icon:FACEBOOK"),
    FACEBOOK_F("font-icon:FACEBOOK_F"),
    FACEBOOK_OFFICIAL("font-icon:FACEBOOK_OFFICIAL"),
    FACEBOOK_SQUARE("font-icon:FACEBOOK_SQUARE"),
    FAST_BACKWARD("font-icon:FAST_BACKWARD"),
    FAST_FORWARD("font-icon:FAST_FORWARD"),
    FAX("font-icon:FAX"),
    FEED("font-icon:FEED"),
    FEMALE("font-icon:FEMALE"),
    FIGHTER_JET("font-icon:FIGHTER_JET"),
    FILE("font-icon:FILE"),
    FILE_ARCHIVE_O("font-icon:FILE_ARCHIVE_O"),
    FILE_AUDIO_O("font-icon:FILE_AUDIO_O"),
    FILE_CODE_O("font-icon:FILE_CODE_O"),
    FILE_EXCEL_O("font-icon:FILE_EXCEL_O"),
    FILE_IMAGE_O("font-icon:FILE_IMAGE_O"),
    FILE_MOVIE_O("font-icon:FILE_MOVIE_O"),
    FILE_O("font-icon:FILE_O"),
    FILE_PDF_O("font-icon:FILE_PDF_O"),
    FILE_PHOTO_O("font-icon:FILE_PHOTO_O"),
    FILE_PICTURE_O("font-icon:FILE_PICTURE_O"),
    FILE_POWERPOINT_O("font-icon:FILE_POWERPOINT_O"),
    FILE_SOUND_O("font-icon:FILE_SOUND_O"),
    FILE_TEXT("font-icon:FILE_TEXT"),
    FILE_TEXT_O("font-icon:FILE_TEXT_O"),
    FILE_VIDEO_O("font-icon:FILE_VIDEO_O"),
    FILE_WORD_O("font-icon:FILE_WORD_O"),
    FILE_ZIP_O("font-icon:FILE_ZIP_O"),
    FILES_O("font-icon:FILES_O"),
    FILM("font-icon:FILM"),
    FILTER("font-icon:FILTER"),
    FIRE("font-icon:FIRE"),
    FIRE_EXTINGUISHER("font-icon:FIRE_EXTINGUISHER"),
    FIREFOX("font-icon:FIREFOX"),
    FIRST_ORDER("font-icon:FIRST_ORDER"),
    FLAG("font-icon:FLAG"),
    FLAG_CHECKERED("font-icon:FLAG_CHECKERED"),
    FLAG_O("font-icon:FLAG_O"),
    FLASH("font-icon:FLASH"),
    FLASK("font-icon:FLASK"),
    FLICKR("font-icon:FLICKR"),
    FLOPPY_O("font-icon:FLOPPY_O"),
    FOLDER("font-icon:FOLDER"),
    FOLDER_O("font-icon:FOLDER_O"),
    FOLDER_OPEN("font-icon:FOLDER_OPEN"),
    FOLDER_OPEN_O("font-icon:FOLDER_OPEN_O"),
    FONT("font-icon:FONT"),
    FONTICONS("font-icon:FONTICONS"),
    FONT_AWESOME("font-icon:FONT_AWESOME"),
    FORT_AWESOME("font-icon:FORT_AWESOME"),
    FORUMBEE("font-icon:FORUMBEE"),
    FORWARD("font-icon:FORWARD"),
    FOURSQUARE("font-icon:FOURSQUARE"),
    FREE_CODE_CAMP("font-icon:FREE_CODE_CAMP"),
    FROWN_O("font-icon:FROWN_O"),
    FUTBOL_O("font-icon:FUTBOL_O"),
    GAMEPAD("font-icon:GAMEPAD"),
    GAVEL("font-icon:GAVEL"),
    GBP("font-icon:GBP"),
    GE("font-icon:GE"),
    GEAR("font-icon:GEAR"),
    GEARS("font-icon:GEARS"),
    GENDERLESS("font-icon:GENDERLESS"),
    GET_POCKET("font-icon:GET_POCKET"),
    GG("font-icon:GG"),
    GG_CIRCLE("font-icon:GG_CIRCLE"),
    GIFT("font-icon:GIFT"),
    GIT("font-icon:GIT"),
    GIT_SQUARE("font-icon:GIT_SQUARE"),
    GITHUB("font-icon:GITHUB"),
    GITHUB_ALT("font-icon:GITHUB_ALT"),
    GITHUB_SQUARE("font-icon:GITHUB_SQUARE"),
    GITLAB("font-icon:GITLAB"),
    GITTIP("font-icon:GITTIP"),
    GLASS("font-icon:GLASS"),
    GLIDE("font-icon:GLIDE"),
    GLIDE_G("font-icon:GLIDE_G"),
    GLOBE("font-icon:GLOBE"),
    GOOGLE("font-icon:GOOGLE"),
    GOOGLE_PLUS("font-icon:GOOGLE_PLUS"),
    GOOGLE_PLUS_CIRCLE("font-icon:GOOGLE_PLUS_CIRCLE"),
    GOOGLE_PLUS_OFFICIAL("font-icon:GOOGLE_PLUS_OFFICIAL"),
    GOOGLE_PLUS_SQUARE("font-icon:GOOGLE_PLUS_SQUARE"),
    GOOGLE_WALLET("font-icon:GOOGLE_WALLET"),
    GRADUATION_CAP("font-icon:GRADUATION_CAP"),
    GRATIPAY("font-icon:GRATIPAY"),
    GRAV("font-icon:GRAV"),
    GROUP("font-icon:GROUP"),
    H_SQUARE("font-icon:H_SQUARE"),
    HACKER_NEWS("font-icon:HACKER_NEWS"),
    HAND_GRAB_O("font-icon:HAND_GRAB_O"),
    HAND_LIZARD_O("font-icon:HAND_LIZARD_O"),
    HAND_O_DOWN("font-icon:HAND_O_DOWN"),
    HAND_O_LEFT("font-icon:HAND_O_LEFT"),
    HAND_O_RIGHT("font-icon:HAND_O_RIGHT"),
    HAND_O_UP("font-icon:HAND_O_UP"),
    HAND_PAPER_O("font-icon:HAND_PAPER_O"),
    HAND_PEACE_O("font-icon:HAND_PEACE_O"),
    HAND_POINTER_O("font-icon:HAND_POINTER_O"),
    HAND_ROCK_O("font-icon:HAND_ROCK_O"),
    HAND_SCISSORS_O("font-icon:HAND_SCISSORS_O"),
    HAND_SPOCK_O("font-icon:HAND_SPOCK_O"),
    HAND_STOP_O("font-icon:HAND_STOP_O"),
    HANDSHAKE_O("font-icon:HANDSHAKE_O"),
    HARD_OF_HEARING("font-icon:HARD_OF_HEARING"),
    HASHTAG("font-icon:HASHTAG"),
    HDD_O("font-icon:HDD_O"),
    HEADER("font-icon:HEADER"),
    HEADPHONES("font-icon:HEADPHONES"),
    HEART("font-icon:HEART"),
    HEART_O("font-icon:HEART_O"),
    HEARTBEAT("font-icon:HEARTBEAT"),
    HISTORY("font-icon:HISTORY"),
    HOME("font-icon:HOME"),
    HOSPITAL_O("font-icon:HOSPITAL_O"),
    HOTEL("font-icon:HOTEL"),
    HOURGLASS("font-icon:HOURGLASS"),
    HOURGLASS_1("font-icon:HOURGLASS_1"),
    HOURGLASS_2("font-icon:HOURGLASS_2"),
    HOURGLASS_3("font-icon:HOURGLASS_3"),
    HOURGLASS_END("font-icon:HOURGLASS_END"),
    HOURGLASS_HALF("font-icon:HOURGLASS_HALF"),
    HOURGLASS_O("font-icon:HOURGLASS_O"),
    HOURGLASS_START("font-icon:HOURGLASS_START"),
    HOUZZ("font-icon:HOUZZ"),
    HTML5("font-icon:HTML5"),
    I_CURSOR("font-icon:I_CURSOR"),
    ID_BADGE("font-icon:ID_BADGE"),
    ID_CARD("font-icon:ID_CARD"),
    ID_CARD_O("font-icon:ID_CARD_O"),
    ILS("font-icon:ILS"),
    IMAGE("font-icon:IMAGE"),
    IMDB("font-icon:IMDB"),
    INBOX("font-icon:INBOX"),
    INDENT("font-icon:INDENT"),
    INDUSTRY("font-icon:INDUSTRY"),
    INFO("font-icon:INFO"),
    INFO_CIRCLE("font-icon:INFO_CIRCLE"),
    INR("font-icon:INR"),
    INSTAGRAM("font-icon:INSTAGRAM"),
    INSTITUTION("font-icon:INSTITUTION"),
    INTERNET_EXPLORER("font-icon:INTERNET_EXPLORER"),
    INTERSEX("font-icon:INTERSEX"),
    IOXHOST("font-icon:IOXHOST"),
    ITALIC("font-icon:ITALIC"),
    JOOMLA("font-icon:JOOMLA"),
    JPY("font-icon:JPY"),
    JSFIDDLE("font-icon:JSFIDDLE"),
    KEY("font-icon:KEY"),
    KEYBOARD_O("font-icon:KEYBOARD_O"),
    KRW("font-icon:KRW"),
    LANGUAGE("font-icon:LANGUAGE"),
    LAPTOP("font-icon:LAPTOP"),
    LASTFM("font-icon:LASTFM"),
    LASTFM_SQUARE("font-icon:LASTFM_SQUARE"),
    LEAF("font-icon:LEAF"),
    LEANPUB("font-icon:LEANPUB"),
    LEGAL("font-icon:LEGAL"),
    LEMON_O("font-icon:LEMON_O"),
    LEVEL_DOWN("font-icon:LEVEL_DOWN"),
    LEVEL_UP("font-icon:LEVEL_UP"),
    LIFE_BOUY("font-icon:LIFE_BOUY"),
    LIFE_BUOY("font-icon:LIFE_BUOY"),
    LIFE_RING("font-icon:LIFE_RING"),
    LIFE_SAVER("font-icon:LIFE_SAVER"),
    LIGHTBULB_O("font-icon:LIGHTBULB_O"),
    LINE_CHART("font-icon:LINE_CHART"),
    LINK("font-icon:LINK"),
    LINKEDIN("font-icon:LINKEDIN"),
    LINKEDIN_SQUARE("font-icon:LINKEDIN_SQUARE"),
    LINODE("font-icon:LINODE"),
    LINUX("font-icon:LINUX"),
    LIST("font-icon:LIST"),
    LIST_ALT("font-icon:LIST_ALT"),
    LIST_OL("font-icon:LIST_OL"),
    LIST_UL("font-icon:LIST_UL"),
    LOCATION_ARROW("font-icon:LOCATION_ARROW"),
    LOCK("font-icon:LOCK"),
    LONG_ARROW_DOWN("font-icon:LONG_ARROW_DOWN"),
    LONG_ARROW_LEFT("font-icon:LONG_ARROW_LEFT"),
    LONG_ARROW_RIGHT("font-icon:LONG_ARROW_RIGHT"),
    LONG_ARROW_UP("font-icon:LONG_ARROW_UP"),
    LOW_VISION("font-icon:LOW_VISION"),
    MAGIC("font-icon:MAGIC"),
    MAGNET("font-icon:MAGNET"),
    MAIL_FORWARD("font-icon:MAIL_FORWARD"),
    MAIL_REPLY("font-icon:MAIL_REPLY"),
    MAIL_REPLY_ALL("font-icon:MAIL_REPLY_ALL"),
    MALE("font-icon:MALE"),
    MAP("font-icon:MAP"),
    MAP_MARKER("font-icon:MAP_MARKER"),
    MAP_O("font-icon:MAP_O"),
    MAP_PIN("font-icon:MAP_PIN"),
    MAP_SIGNS("font-icon:MAP_SIGNS"),
    MARS("font-icon:MARS"),
    MARS_DOUBLE("font-icon:MARS_DOUBLE"),
    MARS_STROKE("font-icon:MARS_STROKE"),
    MARS_STROKE_H("font-icon:MARS_STROKE_H"),
    MARS_STROKE_V("font-icon:MARS_STROKE_V"),
    MAXCDN("font-icon:MAXCDN"),
    MEANPATH("font-icon:MEANPATH"),
    MEDIUM("font-icon:MEDIUM"),
    MEETUP("font-icon:MEETUP"),
    MEDKIT("font-icon:MEDKIT"),
    MEH_O("font-icon:MEH_O"),
    MERCURY("font-icon:MERCURY"),
    MICROCHIP("font-icon:MICROCHIP"),
    MICROPHONE("font-icon:MICROPHONE"),
    MICROPHONE_SLASH("font-icon:MICROPHONE_SLASH"),
    MINUS("font-icon:MINUS"),
    MINUS_CIRCLE("font-icon:MINUS_CIRCLE"),
    MINUS_SQUARE("font-icon:MINUS_SQUARE"),
    MINUS_SQUARE_O("font-icon:MINUS_SQUARE_O"),
    MIXCLOUD("font-icon:MIXCLOUD"),
    MOBILE("font-icon:MOBILE"),
    MOBILE_PHONE("font-icon:MOBILE_PHONE"),
    MODX("font-icon:MODX"),
    MONEY("font-icon:MONEY"),
    MOON_O("font-icon:MOON_O"),
    MORTAR_BOARD("font-icon:MORTAR_BOARD"),
    MOTORCYCLE("font-icon:MOTORCYCLE"),
    MOUSE_POINTER("font-icon:MOUSE_POINTER"),
    MUSIC("font-icon:MUSIC"),
    NAVICON("font-icon:NAVICON"),
    NEUTER("font-icon:NEUTER"),
    NEWSPAPER_O("font-icon:NEWSPAPER_O"),
    OBJECT_GROUP("font-icon:OBJECT_GROUP"),
    OBJECT_UNGROUP("font-icon:OBJECT_UNGROUP"),
    ODNOKLASSNIKI("font-icon:ODNOKLASSNIKI"),
    ODNOKLASSNIKI_SQUARE("font-icon:ODNOKLASSNIKI_SQUARE"),
    OPENCART("font-icon:OPENCART"),
    OPENID("font-icon:OPENID"),
    OPERA("font-icon:OPERA"),
    OPTIN_MONSTER("font-icon:OPTIN_MONSTER"),
    OUTDENT("font-icon:OUTDENT"),
    PAGELINES("font-icon:PAGELINES"),
    PAINT_BRUSH("font-icon:PAINT_BRUSH"),
    PAPER_PLANE("font-icon:PAPER_PLANE"),
    PAPER_PLANE_O("font-icon:PAPER_PLANE_O"),
    PAPERCLIP("font-icon:PAPERCLIP"),
    PARAGRAPH("font-icon:PARAGRAPH"),
    PASTE("font-icon:PASTE"),
    PAUSE("font-icon:PAUSE"),
    PAUSE_CIRCLE("font-icon:PAUSE_CIRCLE"),
    PAUSE_CIRCLE_O("font-icon:PAUSE_CIRCLE_O"),
    PAW("font-icon:PAW"),
    PAYPAL("font-icon:PAYPAL"),
    PENCIL("font-icon:PENCIL"),
    PENCIL_SQUARE("font-icon:PENCIL_SQUARE"),
    PENCIL_SQUARE_O("font-icon:PENCIL_SQUARE_O"),
    PERCENT("font-icon:PERCENT"),
    PHONE("font-icon:PHONE"),
    PHONE_SQUARE("font-icon:PHONE_SQUARE"),
    PHOTO("font-icon:PHOTO"),
    PICTURE_O("font-icon:PICTURE_O"),
    PIE_CHART("font-icon:PIE_CHART"),
    PIED_PIPER("font-icon:PIED_PIPER"),
    PIED_PIPER_ALT("font-icon:PIED_PIPER_ALT"),
    PIED_PIPER_PP("font-icon:PIED_PIPER_PP"),
    PINTEREST("font-icon:PINTEREST"),
    PINTEREST_P("font-icon:PINTEREST_P"),
    PINTEREST_SQUARE("font-icon:PINTEREST_SQUARE"),
    PLANE("font-icon:PLANE"),
    PLAY("font-icon:PLAY"),
    PLAY_CIRCLE("font-icon:PLAY_CIRCLE"),
    PLAY_CIRCLE_O("font-icon:PLAY_CIRCLE_O"),
    PLUG("font-icon:PLUG"),
    PLUS("font-icon:PLUS"),
    PLUS_CIRCLE("font-icon:PLUS_CIRCLE"),
    PLUS_SQUARE("font-icon:PLUS_SQUARE"),
    PLUS_SQUARE_O("font-icon:PLUS_SQUARE_O"),
    PODCAST("font-icon:PODCAST"),
    POWER_OFF("font-icon:POWER_OFF"),
    PRINT("font-icon:PRINT"),
    PRODUCT_HUNT("font-icon:PRODUCT_HUNT"),
    PUZZLE_PIECE("font-icon:PUZZLE_PIECE"),
    QQ("font-icon:QQ"),
    QUORA("font-icon:QUORA"),
    QRCODE("font-icon:QRCODE"),
    QUESTION("font-icon:QUESTION"),
    QUESTION_CIRCLE("font-icon:QUESTION_CIRCLE"),
    QUESTION_CIRCLE_O("font-icon:QUESTION_CIRCLE_O"),
    QUOTE_LEFT("font-icon:QUOTE_LEFT"),
    QUOTE_RIGHT("font-icon:QUOTE_RIGHT"),
    RA("font-icon:RA"),
    RANDOM("font-icon:RANDOM"),
    RAVELRY("font-icon:RAVELRY"),
    REBEL("font-icon:REBEL"),
    RECYCLE("font-icon:RECYCLE"),
    REDDIT("font-icon:REDDIT"),
    REDDIT_ALIEN("font-icon:REDDIT_ALIEN"),
    REDDIT_SQUARE("font-icon:REDDIT_SQUARE"),
    REFRESH("font-icon:REFRESH"),
    REGISTERED("font-icon:REGISTERED"),
    REMOVE("font-icon:REMOVE"),
    RENREN("font-icon:RENREN"),
    RESISTANCE("font-icon:RESISTANCE"),
    REORDER("font-icon:REORDER"),
    REPEAT("font-icon:REPEAT"),
    REPLY("font-icon:REPLY"),
    REPLY_ALL("font-icon:REPLY_ALL"),
    RETWEET("font-icon:RETWEET"),
    RMB("font-icon:RMB"),
    ROAD("font-icon:ROAD"),
    ROCKET("font-icon:ROCKET"),
    ROTATE_LEFT("font-icon:ROTATE_LEFT"),
    ROTATE_RIGHT("font-icon:ROTATE_RIGHT"),
    ROUBLE("font-icon:ROUBLE"),
    RSS("font-icon:RSS"),
    RSS_SQUARE("font-icon:RSS_SQUARE"),
    RUB("font-icon:RUB"),
    RUBLE("font-icon:RUBLE"),
    RUPEE("font-icon:RUPEE"),
    S15("font-icon:S15"),
    SAFARI("font-icon:SAFARI"),
    SAVE("font-icon:SAVE"),
    SCISSORS("font-icon:SCISSORS"),
    SCRIBD("font-icon:SCRIBD"),
    SEARCH("font-icon:SEARCH"),
    SEARCH_MINUS("font-icon:SEARCH_MINUS"),
    SEARCH_PLUS("font-icon:SEARCH_PLUS"),
    SELLSY("font-icon:SELLSY"),
    SEND("font-icon:SEND"),
    SEND_O("font-icon:SEND_O"),
    SERVER("font-icon:SERVER"),
    SHARE("font-icon:SHARE"),
    SHARE_ALT("font-icon:SHARE_ALT"),
    SHARE_ALT_SQUARE("font-icon:SHARE_ALT_SQUARE"),
    SHARE_SQUARE("font-icon:SHARE_SQUARE"),
    SHARE_SQUARE_O("font-icon:SHARE_SQUARE_O"),
    SHEKEL("font-icon:SHEKEL"),
    SHEQEL("font-icon:SHEQEL"),
    SHIELD("font-icon:SHIELD"),
    SHIP("font-icon:SHIP"),
    SHIRTSINBULK("font-icon:SHIRTSINBULK"),
    SHOPPING_BAG("font-icon:SHOPPING_BAG"),
    SHOPPING_BASKET("font-icon:SHOPPING_BASKET"),
    SHOPPING_CART("font-icon:SHOPPING_CART"),
    SHOWER("font-icon:SHOWER"),
    SIGN_IN("font-icon:SIGN_IN"),
    SIGN_LANGUAGE("font-icon:SIGN_LANGUAGE"),
    SIGN_OUT("font-icon:SIGN_OUT"),
    SIGNAL("font-icon:SIGNAL"),
    SIGNING("font-icon:SIGNING"),
    SIMPLYBUILT("font-icon:SIMPLYBUILT"),
    SITEMAP("font-icon:SITEMAP"),
    SKYATLAS("font-icon:SKYATLAS"),
    SKYPE("font-icon:SKYPE"),
    SLACK("font-icon:SLACK"),
    SLIDERS("font-icon:SLIDERS"),
    SLIDESHARE("font-icon:SLIDESHARE"),
    SMILE_O("font-icon:SMILE_O"),
    SNOWFLAKE_O("font-icon:SNOWFLAKE_O"),
    SNAPCHAT("font-icon:SNAPCHAT"),
    SNAPCHAT_GHOST("font-icon:SNAPCHAT_GHOST"),
    SNAPCHAT_SQUARE("font-icon:SNAPCHAT_SQUARE"),
    SOCCER_BALL_O("font-icon:SOCCER_BALL_O"),
    SORT("font-icon:SORT"),
    SORT_ALPHA_ASC("font-icon:SORT_ALPHA_ASC"),
    SORT_ALPHA_DESC("font-icon:SORT_ALPHA_DESC"),
    SORT_AMOUNT_ASC("font-icon:SORT_AMOUNT_ASC"),
    SORT_AMOUNT_DESC("font-icon:SORT_AMOUNT_DESC"),
    SORT_ASC("font-icon:SORT_ASC"),
    SORT_DESC("font-icon:SORT_DESC"),
    SORT_DOWN("font-icon:SORT_DOWN"),
    SORT_NUMERIC_ASC("font-icon:SORT_NUMERIC_ASC"),
    SORT_NUMERIC_DESC("font-icon:SORT_NUMERIC_DESC"),
    SORT_UP("font-icon:SORT_UP"),
    SOUNDCLOUD("font-icon:SOUNDCLOUD"),
    SPACE_SHUTTLE("font-icon:SPACE_SHUTTLE"),
    SPINNER("font-icon:SPINNER"),
    SPOON("font-icon:SPOON"),
    SPOTIFY("font-icon:SPOTIFY"),
    SQUARE("font-icon:SQUARE"),
    SQUARE_O("font-icon:SQUARE_O"),
    STACK_EXCHANGE("font-icon:STACK_EXCHANGE"),
    STACK_OVERFLOW("font-icon:STACK_OVERFLOW"),
    STAR("font-icon:STAR"),
    STAR_HALF("font-icon:STAR_HALF"),
    STAR_HALF_EMPTY("font-icon:STAR_HALF_EMPTY"),
    STAR_HALF_FULL("font-icon:STAR_HALF_FULL"),
    STAR_HALF_O("font-icon:STAR_HALF_O"),
    STAR_O("font-icon:STAR_O"),
    STEAM("font-icon:STEAM"),
    STEAM_SQUARE("font-icon:STEAM_SQUARE"),
    STEP_BACKWARD("font-icon:STEP_BACKWARD"),
    STEP_FORWARD("font-icon:STEP_FORWARD"),
    STETHOSCOPE("font-icon:STETHOSCOPE"),
    STICKY_NOTE("font-icon:STICKY_NOTE"),
    STICKY_NOTE_O("font-icon:STICKY_NOTE_O"),
    STOP("font-icon:STOP"),
    STOP_CIRCLE("font-icon:STOP_CIRCLE"),
    STOP_CIRCLE_O("font-icon:STOP_CIRCLE_O"),
    STREET_VIEW("font-icon:STREET_VIEW"),
    STRIKETHROUGH("font-icon:STRIKETHROUGH"),
    STUMBLEUPON("font-icon:STUMBLEUPON"),
    STUMBLEUPON_CIRCLE("font-icon:STUMBLEUPON_CIRCLE"),
    SUBSCRIPT("font-icon:SUBSCRIPT"),
    SUBWAY("font-icon:SUBWAY"),
    SUITCASE("font-icon:SUITCASE"),
    SUN_O("font-icon:SUN_O"),
    SUPERPOWERS("font-icon:SUPERPOWERS"),
    SUPERSCRIPT("font-icon:SUPERSCRIPT"),
    SUPPORT("font-icon:SUPPORT"),
    TABLE("font-icon:TABLE"),
    TABLET("font-icon:TABLET"),
    TACHOMETER("font-icon:TACHOMETER"),
    TAG("font-icon:TAG"),
    TAGS("font-icon:TAGS"),
    TASKS("font-icon:TASKS"),
    TAXI("font-icon:TAXI"),
    TELEGRAM("font-icon:TELEGRAM"),
    TELEVISION("font-icon:TELEVISION"),
    TENCENT_WEIBO("font-icon:TENCENT_WEIBO"),
    TERMINAL("font-icon:TERMINAL"),
    TEXT_HEIGHT("font-icon:TEXT_HEIGHT"),
    TEXT_WIDTH("font-icon:TEXT_WIDTH"),
    TH("font-icon:TH"),
    TH_LARGE("font-icon:TH_LARGE"),
    TH_LIST("font-icon:TH_LIST"),
    THERMOMETER("font-icon:THERMOMETER"),
    THERMOMETER_O("font-icon:THERMOMETER_O"),
    THERMOMETER_1("font-icon:THERMOMETER_1"),
    THERMOMETER_2("font-icon:THERMOMETER_2"),
    THERMOMETER_3("font-icon:THERMOMETER_3"),
    THERMOMETER_4("font-icon:THERMOMETER_4"),
    THERMOMETER_EMPTY("font-icon:THERMOMETER_EMPTY"),
    THERMOMETER_FULL("font-icon:THERMOMETER_FULL"),
    THERMOMETER_HALF("font-icon:THERMOMETER_HALF"),
    THERMOMETER_QUARTER("font-icon:THERMOMETER_QUARTER"),
    THERMOMETER_THREE_QUARTERS("font-icon:THERMOMETER_THREE_QUARTERS"),
    THUMB_TACK("font-icon:THUMB_TACK"),
    THUMBS_DOWN("font-icon:THUMBS_DOWN"),
    THUMBS_O_DOWN("font-icon:THUMBS_O_DOWN"),
    THUMBS_O_UP("font-icon:THUMBS_O_UP"),
    THUMBS_UP("font-icon:THUMBS_UP"),
    TICKET("font-icon:TICKET"),
    TIMES("font-icon:TIMES"),
    TIMES_CIRCLE("font-icon:TIMES_CIRCLE"),
    TIMES_CIRCLE_O("font-icon:TIMES_CIRCLE_O"),
    TIMES_RECTANGLE("font-icon:TIMES_RECTANGLE"),
    TIMES_RECTANGLE_O("font-icon:TIMES_RECTANGLE_O"),
    TINT("font-icon:TINT"),
    TOGGLE_DOWN("font-icon:TOGGLE_DOWN"),
    TOGGLE_LEFT("font-icon:TOGGLE_LEFT"),
    TOGGLE_OFF("font-icon:TOGGLE_OFF"),
    TOGGLE_ON("font-icon:TOGGLE_ON"),
    TOGGLE_RIGHT("font-icon:TOGGLE_RIGHT"),
    TOGGLE_UP("font-icon:TOGGLE_UP"),
    TRADEMARK("font-icon:TRADEMARK"),
    TRAIN("font-icon:TRAIN"),
    TRANSGENDER("font-icon:TRANSGENDER"),
    TRANSGENDER_ALT("font-icon:TRANSGENDER_ALT"),
    TRASH("font-icon:TRASH"),
    TRASH_O("font-icon:TRASH_O"),
    TREE("font-icon:TREE"),
    TRELLO("font-icon:TRELLO"),
    TRIPADVISOR("font-icon:TRIPADVISOR"),
    TROPHY("font-icon:TROPHY"),
    TRUCK("font-icon:TRUCK"),
    TRY("font-icon:TRY"),
    TTY("font-icon:TTY"),
    TUMBLR("font-icon:TUMBLR"),
    TUMBLR_SQUARE("font-icon:TUMBLR_SQUARE"),
    TURKISH_LIRA("font-icon:TURKISH_LIRA"),
    TV("font-icon:TV"),
    TWITCH("font-icon:TWITCH"),
    TWITTER("font-icon:TWITTER"),
    TWITTER_SQUARE("font-icon:TWITTER_SQUARE"),
    UMBRELLA("font-icon:UMBRELLA"),
    UNDERLINE("font-icon:UNDERLINE"),
    UNDO("font-icon:UNDO"),
    UNIVERSAL_ACCESS("font-icon:UNIVERSAL_ACCESS"),
    UNIVERSITY("font-icon:UNIVERSITY"),
    UNLINK("font-icon:UNLINK"),
    UNLOCK("font-icon:UNLOCK"),
    UNLOCK_ALT("font-icon:UNLOCK_ALT"),
    UNSORTED("font-icon:UNSORTED"),
    UPLOAD("font-icon:UPLOAD"),
    USB("font-icon:USB"),
    USD("font-icon:USD"),
    USER("font-icon:USER"),
    USER_CIRCLE("font-icon:USER_CIRCLE"),
    USER_CIRCLE_O("font-icon:USER_CIRCLE_O"),
    USER_O("font-icon:USER_O"),
    USER_MD("font-icon:USER_MD"),
    USER_PLUS("font-icon:USER_PLUS"),
    USER_SECRET("font-icon:USER_SECRET"),
    USER_TIMES("font-icon:USER_TIMES"),
    USERS("font-icon:USERS"),
    VCARD("font-icon:VCARD"),
    VCARD_O("font-icon:VCARD_O"),
    VENUS("font-icon:VENUS"),
    VENUS_DOUBLE("font-icon:VENUS_DOUBLE"),
    VENUS_MARS("font-icon:VENUS_MARS"),
    VIACOIN("font-icon:VIACOIN"),
    VIADEO("font-icon:VIADEO"),
    VIADEO_SQUARE("font-icon:VIADEO_SQUARE"),
    VIDEO_CAMERA("font-icon:VIDEO_CAMERA"),
    VIMEO("font-icon:VIMEO"),
    VIMEO_SQUARE("font-icon:VIMEO_SQUARE"),
    VINE("font-icon:VINE"),
    VK("font-icon:VK"),
    VOLUME_CONTROL_PHONE("font-icon:VOLUME_CONTROL_PHONE"),
    VOLUME_DOWN("font-icon:VOLUME_DOWN"),
    VOLUME_OFF("font-icon:VOLUME_OFF"),
    VOLUME_UP("font-icon:VOLUME_UP"),
    WARNING("font-icon:WARNING"),
    WECHAT("font-icon:WECHAT"),
    WEIBO("font-icon:WEIBO"),
    WEIXIN("font-icon:WEIXIN"),
    WHATSAPP("font-icon:WHATSAPP"),
    WHEELCHAIR("font-icon:WHEELCHAIR"),
    WHEELCHAIR_ALT("font-icon:WHEELCHAIR_ALT"),
    WIFI("font-icon:WIFI"),
    WIKIPEDIA_W("font-icon:WIKIPEDIA_W"),
    WINDOWS("font-icon:WINDOWS"),
    WINDOW_CLOSE("font-icon:WINDOW_CLOSE"),
    WINDOW_CLOSE_O("font-icon:WINDOW_CLOSE_O"),
    WINDOW_MAXIMIZE("font-icon:WINDOW_MAXIMIZE"),
    WINDOW_MINIMIZE("font-icon:WINDOW_MINIMIZE"),
    WINDOW_RESTORE("font-icon:WINDOW_RESTORE"),
    WON("font-icon:WON"),
    WORDPRESS("font-icon:WORDPRESS"),
    WPBEGINNER("font-icon:WPBEGINNER"),
    WPEXPLORER("font-icon:WPEXPLORER"),
    WPFORMS("font-icon:WPFORMS"),
    WRENCH("font-icon:WRENCH"),
    XING("font-icon:XING"),
    XING_SQUARE("font-icon:XING_SQUARE"),
    Y_COMBINATOR("font-icon:Y_COMBINATOR"),
    Y_COMBINATOR_SQUARE("font-icon:Y_COMBINATOR_SQUARE"),
    YAHOO("font-icon:YAHOO"),
    YC("font-icon:YC"),
    YC_SQUARE("font-icon:YC_SQUARE"),
    YELP("font-icon:YELP"),
    YEN("font-icon:YEN"),
    YOAST("font-icon:YOAST"),
    YOUTUBE("font-icon:YOUTUBE"),
    YOUTUBE_PLAY("font-icon:YOUTUBE_PLAY"),
    YOUTUBE_SQUARE("font-icon:YOUTUBE_SQUARE");

    protected String source;

    JmixIcon(String source) {
        this.source = source;
    }

    @Override
    public String source() {
        return source;
    }
}
