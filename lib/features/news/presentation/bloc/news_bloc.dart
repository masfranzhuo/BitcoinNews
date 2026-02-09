import 'package:bloc/bloc.dart';

import '../../../../core/constants.dart';
import '../../domain/usecases/get_top_headlines.dart';
import 'news_event.dart';
import 'news_state.dart';

class NewsBloc extends Bloc<NewsEvent, NewsState> {
  NewsBloc({required this.getTopHeadlines}) : super(NewsState.initial()) {
    on<NewsRequested>(_onRequested);
    on<NewsNextPageRequested>(_onNextPageRequested);
    on<NewsRefreshed>(_onRefreshed);
  }

  final GetTopHeadlines getTopHeadlines;

  Future<void> _onRequested(
    NewsRequested event,
    Emitter<NewsState> emit,
  ) async {
    emit(state.copyWith(status: NewsStatus.loading));
    await _fetchPage(emit, page: 1, reset: true);
  }

  Future<void> _onNextPageRequested(
    NewsNextPageRequested event,
    Emitter<NewsState> emit,
  ) async {
    if (state.hasReachedMax || state.status == NewsStatus.loading) {
      return;
    }
    await _fetchPage(emit, page: state.page + 1, reset: false);
  }

  Future<void> _onRefreshed(
    NewsRefreshed event,
    Emitter<NewsState> emit,
  ) async {
    emit(state.copyWith(status: NewsStatus.loading));
    await _fetchPage(emit, page: 1, reset: true);
  }

  Future<void> _fetchPage(
    Emitter<NewsState> emit, {
    required int page,
    required bool reset,
  }) async {
    try {
      final results = await getTopHeadlines(
        GetTopHeadlinesParams(page: page, pageSize: newsPageSize),
      );
      final updatedArticles = reset
          ? results
          : List.of(state.articles)..addAll(results);
      emit(
        state.copyWith(
          status: NewsStatus.success,
          articles: updatedArticles,
          page: page,
          hasReachedMax: results.length < newsPageSize,
        ),
      );
    } catch (error) {
      emit(
        state.copyWith(
          status: NewsStatus.failure,
          errorMessage: error.toString(),
        ),
      );
    }
  }
}
