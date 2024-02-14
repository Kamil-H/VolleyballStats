//
//  StatsScreen.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 02/01/2024.
//

import Foundation
import SwiftUI
import SimpleTable
import shared

struct StatsScreen: View {
    
    private let scope: Scope
    private let presenter: StatsPresenter
    
    init(presentersFactory: PresentersFactory, screen: ScreenStats) {
        self.scope = presentersFactory.createScope()
        self.presenter = presentersFactory.createStatsPresenter(scope: scope, screen: screen)
    }
    
    var body: some View {
        Screen(
            stateFlow: presenter.state,
            scope: scope,
            onFabButtonClicked: { presenter.onFabButtonClicked() }
        ) { state in
            content(state: state)
        }
    }
    
    @ViewBuilder
    private func content(state: StatsState) -> some View {
        if state.tableContent.rows.isEmpty {
            Text("Nothing to show...")
                .font(.largeTitle)
        } else {
            VStack {
                tableContent(tableContent: state.tableContent, color: state.colorAccent)
            }
            .toolbar {
                ToolbarItemGroup(placement: .status) {
                    SelectOptionView(selectOption: state.selectSkillState)
                }
            }
        }
    }
    
    @ViewBuilder
    private func tableContent(tableContent: TableContent, color: ColorAccent) -> some View {
        SimpleTableView {
            SimpleTableLayout(columnsCount: tableContent.header?.cells.count ?? 0) {
                columnHeaders(headerRow: tableContent.header!, color: color)
                ForEach(Array(tableContent.rows.enumerated()), id: \.element) { index, element in
                    row(tableRow: element, index: index)
                }
            }
        }
    }
    
    @ViewBuilder
    private func columnHeaders(headerRow: shared.TableRow<HeaderCell>, color: ColorAccent) -> some View {
        Group {
            ForEach(headerRow.cells) { row in
                Text("\(row.firstLine) \(row.secondLine ?? "")".trimmingCharacters(in: .whitespaces))
                    .font(.headline)
                    .colorInvert()
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
                    .onTapGesture {
                        row.onClick?()
                    }
                    .padding(Dimens.marginSmall)
                    .background(row.selected ? colorContainer(for: color) : colorCell(for: color))
            }
        }
        .simpleTableHeaderRow()
        .zIndex(2)
    }
    
    @ViewBuilder
    private func row(tableRow: shared.TableRow<DataCell>, index: Int) -> some View {
        Group {
            ForEach(tableRow.cells) { row in
                Text(row.content)
                    .padding(.trailing)
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
            }
        }
        .font(.body)
        .padding(Dimens.marginSmall)
        .background(Color.onBackground.opacity(index % 2 == 0 ? 0.1 : 0))
        .simpleTableHeaderRow()
    }
}

private func colorCell(for color: ColorAccent) -> Color {
    switch color {
    case .primary:
        return Color.primary
    case .tertiary:
        return Color.tertiary
    case .default:
        return Color.primary
    }
}

private func colorContainer(for color: ColorAccent) -> Color {
    switch color {
    case .primary:
        return Color.primaryContainer
    case .tertiary:
        return Color.tertiaryContainer
    case .default:
        return Color.primaryContainer
    }
}

extension shared.TableRow : Identifiable {
}

extension HeaderCell : Identifiable {
}

extension DataCell : Identifiable {
}
