import '../../../../core/usecases/usecase.dart';
import '../entities/article.dart';
import '../repositories/news_repository.dart';

class GetTopHeadlinesParams {
  const GetTopHeadlinesParams({required this.page, required this.pageSize});

  final int page;
  final int pageSize;
}

class GetTopHeadlines extends UseCase<List<Article>, GetTopHeadlinesParams> {
  GetTopHeadlines(this.repository);

  final NewsRepository repository;

  @override
  Future<List<Article>> call(GetTopHeadlinesParams params) {
    return repository.getTopHeadlines(
      page: params.page,
      pageSize: params.pageSize,
    );
  }
}
