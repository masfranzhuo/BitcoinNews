import '../../domain/entities/article.dart';
import '../../domain/repositories/news_repository.dart';
import '../datasources/news_remote_data_source.dart';

class NewsRepositoryImpl implements NewsRepository {
  NewsRepositoryImpl({required this.remoteDataSource});

  final NewsRemoteDataSource remoteDataSource;

  @override
  Future<List<Article>> getTopHeadlines({
    required int page,
    required int pageSize,
  }) async {
    return remoteDataSource.getTopHeadlines(
      page: page,
      pageSize: pageSize,
    );
  }
}
