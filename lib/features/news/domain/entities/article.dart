import 'package:equatable/equatable.dart';

class Article extends Equatable {
  const Article({
    required this.title,
    required this.source,
    required this.publishedAt,
    required this.url,
    required this.imageUrl,
    required this.description,
  });

  final String title;
  final String source;
  final DateTime publishedAt;
  final String url;
  final String imageUrl;
  final String description;

  @override
  List<Object?> get props => [
        title,
        source,
        publishedAt,
        url,
        imageUrl,
        description,
      ];
}
