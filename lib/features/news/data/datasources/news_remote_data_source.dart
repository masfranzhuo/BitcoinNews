import 'dart:convert';

import 'package:http/http.dart' as http;

import '../models/article_model.dart';

abstract class NewsRemoteDataSource {
  Future<List<ArticleModel>> getTopHeadlines({required int page, required int pageSize});
}

class NewsRemoteDataSourceImpl implements NewsRemoteDataSource {
  NewsRemoteDataSourceImpl({
    required this.baseUrl,
    required this.apiKey,
    http.Client? client,
  }) : _client = client ?? http.Client();

  final String baseUrl;
  final String apiKey;
  final http.Client _client;

  @override
  Future<List<ArticleModel>> getTopHeadlines({
    required int page,
    required int pageSize,
  }) async {
    if (apiKey.isEmpty) {
      throw Exception('Missing NEWS_API_KEY. Use --dart-define=NEWS_API_KEY=...');
    }

    final uri = Uri.parse(
      '$baseUrl/top-headlines?'
      'q=bitcoin&'
      'language=en&'
      'pageSize=$pageSize&'
      'page=$page&'
      'apiKey=$apiKey',
    );

    final response = await _client.get(uri);
    if (response.statusCode != 200) {
      throw Exception('Failed to load news: ${response.statusCode}');
    }

    final payload = jsonDecode(response.body) as Map<String, dynamic>;
    final items = payload['articles'] as List<dynamic>? ?? [];
    return items
        .map((item) => ArticleModel.fromJson(item as Map<String, dynamic>))
        .toList();
  }
}
