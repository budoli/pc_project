import unittest
from types import SimpleNamespace
from unittest.mock import Mock

from domain.chat.chat_service import ChatService
from domain.chat.region import find_region_codes
from domain.chat.schemas import UserProfile

SEOUL_POLICY = SimpleNamespace(payload={"policyId": 1, "title": "서울 월세", "region": "11110,11140"}, score=0.9)
BUSAN_POLICY = SimpleNamespace(payload={"policyId": 2, "title": "부산 월세", "region": "26110,26140"}, score=0.8)
JEONNAM_POLICY = SimpleNamespace(payload={"policyId": 3, "title": "곡성 월세", "region": "12720"}, score=0.7)
NATIONAL_POLICY = SimpleNamespace(payload={"policyId": 4, "title": "국민취업지원", "region": "11110,26110,12720"}, score=0.6)


class FindRegionCodesTest(unittest.TestCase):
    def test_finds_short_and_full_region_names(self):
        self.assertEqual(find_region_codes("서울 사는 27살인데 월세 지원 있어?"), ["11"])
        self.assertEqual(find_region_codes("부산광역시 청년 정책"), ["26"])
        self.assertEqual(find_region_codes("강원도 청년 창업 지원"), ["51"])

    def test_maps_merged_and_special_regions_to_data_codes(self):
        self.assertEqual(find_region_codes("광주 청년 월세"), ["12"])
        self.assertEqual(find_region_codes("전남광주통합특별시 정책"), ["12"])
        self.assertEqual(find_region_codes("전북 청년"), ["52"])
        self.assertEqual(find_region_codes("제주 살아요"), ["50"])

    def test_returns_every_region_in_order_without_duplicates(self):
        self.assertEqual(find_region_codes("부산이랑 서울, 그리고 부산광역시"), ["26", "11"])

    def test_ignores_economy_meaning_of_gyeonggi(self):
        self.assertEqual(find_region_codes("요즘 경기가 안 좋아서 취업이 어려워"), [])
        self.assertEqual(find_region_codes("경기도 청년 지원"), ["41"])

    def test_returns_empty_without_region(self):
        self.assertEqual(find_region_codes("월세 지원 받을 수 있어?"), [])
        self.assertEqual(find_region_codes(None), [])


class ChatRegionFilterTest(unittest.TestCase):
    def setUp(self):
        self.embedding_service = Mock()
        self.embedding_service.embed.return_value = [0.1]
        self.qdrant_service = Mock()
        self.qdrant_service.search.return_value = [SEOUL_POLICY, BUSAN_POLICY, JEONNAM_POLICY, NATIONAL_POLICY]
        self.ai_service = Mock()
        self.ai_service.generate.return_value = "답변"
        self.service = ChatService(self.embedding_service, self.qdrant_service, self.ai_service)
        self.seoul_profile = UserProfile(age=27, region="서울특별시", regionCode="11")

    def referenced_ids(self, result):
        return [reference["policyId"] for reference in result["references"]]

    def test_query_region_without_profile_filters_results(self):
        result = self.service.chat("서울 사는데 월세 지원 있어?", profile=None)

        self.assertEqual(self.referenced_ids(result), [1, 4])

    def test_profile_region_is_used_when_query_has_no_region(self):
        result = self.service.chat("월세 지원 있어?", profile=self.seoul_profile)

        self.assertEqual(self.referenced_ids(result), [1, 4])

    def test_query_region_overrides_profile_region(self):
        result = self.service.chat("부산 월세 지원 알려줘", profile=self.seoul_profile)

        self.assertEqual(self.referenced_ids(result), [2, 4])
        search_text = self.embedding_service.embed.call_args.args[0]
        prompt = self.ai_service.generate.call_args.args[0]
        self.assertIn("부산광역시", search_text)
        self.assertNotIn("서울특별시", search_text)
        self.assertNotIn("서울특별시", prompt)

    def test_query_region_does_not_change_callers_profile(self):
        self.service.chat("부산 월세 지원 알려줘", profile=self.seoul_profile)

        self.assertEqual(self.seoul_profile.regionCode, "11")

    def test_no_region_anywhere_keeps_all_results(self):
        result = self.service.chat("월세 지원 있어?", profile=None)

        self.assertEqual(self.referenced_ids(result), [1, 2, 3, 4])


if __name__ == "__main__":
    unittest.main()
