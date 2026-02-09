import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'core/constants.dart';
import 'features/news/data/datasources/news_remote_data_source.dart';
import 'features/news/data/repositories/news_repository_impl.dart';
import 'features/news/domain/usecases/get_top_headlines.dart';
import 'features/news/presentation/bloc/news_bloc.dart';
import 'features/news/presentation/bloc/news_event.dart';
import 'features/news/presentation/pages/news_page.dart';

void main() {
  final remoteDataSource = NewsRemoteDataSourceImpl(
    baseUrl: newsApiBaseUrl,
    apiKey: newsApiKey,
  );
  final repository = NewsRepositoryImpl(remoteDataSource: remoteDataSource);
  final getTopHeadlines = GetTopHeadlines(repository);

  runApp(
    BitcoinNewsApp(getTopHeadlines: getTopHeadlines),
  );
}

class BitcoinNewsApp extends StatelessWidget {
  const BitcoinNewsApp({super.key, required this.getTopHeadlines});

  final GetTopHeadlines getTopHeadlines;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Bitcoin News',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.orange),
        useMaterial3: true,
      ),
      home: BlocProvider(
        create: (_) => NewsBloc(getTopHeadlines: getTopHeadlines)
          ..add(const NewsRequested()),
        child: const NewsPage(),
      ),
    );
  }
}
