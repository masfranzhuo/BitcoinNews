import 'package:equatable/equatable.dart';

import '../../domain/entities/article.dart';

enum NewsStatus { initial, loading, success, failure }

class NewsState extends Equatable {
  const NewsState({
    required this.status,
    required this.articles,
    required this.page,
    required this.hasReachedMax,
    this.errorMessage,
  });

  factory NewsState.initial() {
    return const NewsState(
      status: NewsStatus.initial,
      articles: [],
      page: 1,
      hasReachedMax: false,
    );
  }

  final NewsStatus status;
  final List<Article> articles;
  final int page;
  final bool hasReachedMax;
  final String? errorMessage;

  NewsState copyWith({
    NewsStatus? status,
    List<Article>? articles,
    int? page,
    bool? hasReachedMax,
    String? errorMessage,
  }) {
    return NewsState(
      status: status ?? this.status,
      articles: articles ?? this.articles,
      page: page ?? this.page,
      hasReachedMax: hasReachedMax ?? this.hasReachedMax,
      errorMessage: errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, articles, page, hasReachedMax, errorMessage];
}
