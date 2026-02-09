import '../entities/article.dart';

abstract class NewsRepository {
  Future<List<Article>> getTopHeadlines({required int page, required int pageSize});
}
