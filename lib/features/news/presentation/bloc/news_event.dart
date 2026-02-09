import 'package:equatable/equatable.dart';

sealed class NewsEvent extends Equatable {
  const NewsEvent();

  @override
  List<Object?> get props => [];
}

class NewsRequested extends NewsEvent {
  const NewsRequested();
}

class NewsNextPageRequested extends NewsEvent {
  const NewsNextPageRequested();
}

class NewsRefreshed extends NewsEvent {
  const NewsRefreshed();
}
