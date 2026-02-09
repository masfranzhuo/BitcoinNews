import '../../domain/entities/article.dart';

class ArticleModel extends Article {
  const ArticleModel({
    required super.title,
    required super.source,
    required super.publishedAt,
    required super.url,
    required super.imageUrl,
    required super.description,
  });

  factory ArticleModel.fromJson(Map<String, dynamic> json) {
    return ArticleModel(
      title: json['title'] as String? ?? 'Untitled',
      source: (json['source'] as Map<String, dynamic>?)?['name'] as String? ??
          'Unknown',
      publishedAt: DateTime.tryParse(json['publishedAt'] as String? ?? '') ??
          DateTime.fromMillisecondsSinceEpoch(0),
      url: json['url'] as String? ?? '',
      imageUrl: json['urlToImage'] as String? ?? '',
      description: json['description'] as String? ?? '',
    );
  }
}
