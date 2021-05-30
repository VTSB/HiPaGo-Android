package com.vtsb.hipago.data.datasource.remote.service.converter

import javax.inject.Inject
import javax.inject.Singleton


// FROM : https://mrsohn.tistory.com/entry/%E3%84%B4%E3%85%87%E3%84%B9%E3%85%81%E3%85%87%E3%84%B4%E3%84%B9
@Singleton
class KoreanQueryConverter @Inject constructor() {

    //public static final int EVENT_CODE_LENGTH = 6;

    //public static final int DIGIT_BEGIN_UNICODE = 0x30; // 0
    //public static final int DIGIT_END_UNICODE = 0x3A; // 9

    //public static final int EVENT_CODE_LENGTH = 6;
    //public static final int DIGIT_BEGIN_UNICODE = 0x30; // 0
    //public static final int DIGIT_END_UNICODE = 0x3A; // 9
    private val QUERY_DEL_LIM = 39 // '

    //public static final int LARGE_ALPHA_BEGIN_UNICODE = 0;

    //public static final int LARGE_ALPHA_BEGIN_UNICODE = 0;
    private val HANGUL_BEGIN_UNICODE = 0xAC00 // 가

    private val HANGUL_END_UNICODE = 0xD7A3 // ?

    private val HANGUL_CHO_UNIT = 588 // 한글 초성글자간 간격

    private val HANGUL_JUNG_UNIT = 28 // 한글 중성글자간 간격


    private val CHO_LIST = charArrayOf('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')
    private val CHO_SEARCH_LIST = booleanArrayOf(
        true, false, true, true, false, true, true, true, false, true,
        false, true, true, false, true, true, true, true, true
    )

    /**
     * 문자를 유니코드(10진수)로 변환 후 반환한다.
     *
     * @param ch 문자
     * @return 10진수 유니코드
     */
    private fun convertCharToUnicode(ch: Char): Int {
        return ch.toInt()
    }

    /**
     * 10진수를 16진수 문자열로 변환한다.
     *
     * @param decimal 10진수 숫자
     * @return 16진수 문자열
     */
    private fun toHexString(decimal: Int): String {
        //Long intDec = Long.valueOf(decimal);
        return Integer.toHexString(decimal)
    }

    /**
     * 유니코드(16진수)를 문자로 변환 후 반환한다.
     *
     * @param hexUnicode Unicode Hex String
     * @return 문자값
     */
    private fun convertUnicodeToChar(hexUnicode: String): Char {
        return hexUnicode.toInt(16).toChar()
    }

    /**
     * 유니코드(10진수)를 문자로 변환 후 반환한다.
     *
     * @param unicode Unicode String
     * @return 문자값
     */
    private fun convertUnicodeToChar(unicode: Int): Char {
        return convertUnicodeToChar(toHexString(unicode))
    }

//    public SimpleSQLiteQuery makeSimpleSQLiteQuery(String strSearch, String columnName) {
//        return new SimpleSQLiteQuery(makeQuery(strSearch, columnName));
//    }

    //    public SimpleSQLiteQuery makeSimpleSQLiteQuery(String strSearch, String columnName) {
    //        return new SimpleSQLiteQuery(makeQuery(strSearch, columnName));
    //    }
    /**
     * 검색 문자열을 파싱해서 SQL Query 조건 문자열을 만든다.
     *
     * @param strSearch 검색 문자열
     * @return SQL Query 조건 문자열
     */
    fun makeQuery(strSearch: String, columnName: String): String {
        var strSearch = strSearch
        strSearch = strSearch.trim { it <= ' ' } ?: "null"
        val retQuery = StringBuilder()
        var nChoPosition: Int
        var nNextChoPosition: Int
        var StartUnicode: Int
        var EndUnicode: Int
        var nQueryIndex = 0
        val query = StringBuilder()
        for (nIndex in strSearch.indices) {
            nChoPosition = -1
            nNextChoPosition = -1
            StartUnicode = -1
            EndUnicode = -1
            if (strSearch[nIndex].toInt() == QUERY_DEL_LIM) continue
            if (nQueryIndex != 0) {
                query.append(" AND ")
            }
            for (nChoIndex in CHO_LIST.indices) {
                if (strSearch[nIndex] == CHO_LIST[nChoIndex]) {
                    nChoPosition = nChoIndex
                    nNextChoPosition = nChoPosition + 1
                    while (nNextChoPosition < CHO_SEARCH_LIST.size) {
                        if (CHO_SEARCH_LIST[nNextChoPosition]) break
                        nNextChoPosition++
                    }
                    break
                }
            }
            if (nChoPosition >= 0) { // 초성이 있을 경우
                StartUnicode = HANGUL_BEGIN_UNICODE + nChoPosition * HANGUL_CHO_UNIT
                EndUnicode = HANGUL_BEGIN_UNICODE + nNextChoPosition * HANGUL_CHO_UNIT
            } else {
                val Unicode = convertCharToUnicode(strSearch[nIndex])
                if (Unicode in HANGUL_BEGIN_UNICODE..HANGUL_END_UNICODE) {
                    val jong = (Unicode - HANGUL_BEGIN_UNICODE) % HANGUL_CHO_UNIT % HANGUL_JUNG_UNIT
                    if (jong == 0) { // 초성+중성으로 되어 있는 경우
                        StartUnicode = Unicode
                        EndUnicode = Unicode + HANGUL_JUNG_UNIT
                    } else {
                        StartUnicode = Unicode
                        EndUnicode = Unicode
                    }
                }
            }
            if (StartUnicode > 0 && EndUnicode > 0) {
                if (StartUnicode == EndUnicode) query.append("substr(").append(columnName)
                    .append(",").append(nIndex + 1).append(",1)='").append(
                        strSearch[nIndex]
                    ).append("'") else query.append("(substr(").append(columnName).append(",")
                    .append(nIndex + 1).append(",1)>='").append(convertUnicodeToChar(StartUnicode))
                    .append("' AND substr(").append(columnName).append(",").append(nIndex + 1)
                    .append(",1)<'").append(convertUnicodeToChar(EndUnicode)).append("')")
            } else {
                if (Character.isLowerCase(strSearch[nIndex])) { // 영문 소문자
                    query.append("(substr(").append(columnName).append(",").append(nIndex + 1)
                        .append(",1)='").append(
                            strSearch[nIndex]
                        ).append("'").append(" OR substr(").append(columnName).append(",")
                        .append(nIndex + 1).append(",1)='").append(
                            Character.toUpperCase(
                                strSearch[nIndex]
                            )
                        ).append("')")
                } else if (Character.isUpperCase(strSearch[nIndex])) { // 영문 대문자
                    query.append("(substr(").append(columnName).append(",").append(nIndex + 1)
                        .append(",1)='").append(
                            strSearch[nIndex]
                        ).append("'").append(" OR substr(").append(columnName).append(",")
                        .append(nIndex + 1).append(",1)='").append(
                            Character.toLowerCase(
                                strSearch[nIndex]
                            )
                        ).append("')")
                } else  // 기타 문자
                    query.append("substr(").append(columnName).append(",").append(nIndex + 1)
                        .append(",1)='").append(
                            strSearch[nIndex]
                        ).append("'")
            }
            nQueryIndex++
        }
        if (query.isNotEmpty() && strSearch.trim { it <= ' ' }.isNotEmpty()) {
            //if (query.length() > 0 && strSearch != null && strSearch.trim().length() > 0) {
            retQuery.append("(").append(query.toString()).append(")")
            if (strSearch.contains(" ")) {
                // 공백 구분 단어에 대해 단어 모두 포함 검색
                val tokens = strSearch.split(" ").toTypedArray()
                retQuery.append(" OR (")
                var i = 0
                val isSize = tokens.size
                while (i < isSize) {
                    val token = tokens[i]
                    if (i != 0) {
                        retQuery.append(" AND ")
                    }
                    retQuery.append(columnName).append(" like '%").append(token).append("%'")
                    i++
                }
                retQuery.append(")")
            } else {
                // LIKE 검색 추가
                retQuery.append(" OR ").append(columnName).append(" like '%").append(strSearch)
                    .append("%'")
            }
        } else {
            retQuery.append(query.toString())
        }
        return retQuery.toString()
    }
}